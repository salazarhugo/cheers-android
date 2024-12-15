package com.salazar.cheers.data.auth

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import cheers.auth.v1.AuthServiceGrpcKt
import cheers.auth.v1.ListCredentialsRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.salazar.cheers.data.auth.mapper.toGetPasskeyRequest
import com.salazar.cheers.data.auth.mapper.toPasskey
import com.salazar.cheers.data.auth.model.CreatePasskeyRequest
import com.salazar.cheers.data.auth.model.CreatePasskeyResponseData
import com.salazar.cheers.data.auth.model.GetPasskeyResponseData
import com.salazar.cheers.shared.data.BffApiService
import com.salazar.cheers.shared.data.mapper.toCredential
import com.salazar.cheers.shared.data.request.Device
import com.salazar.cheers.shared.data.request.FinishLoginPasskey
import com.salazar.cheers.shared.data.request.FinishLoginRequest
import com.salazar.cheers.shared.data.request.FinishRegistrationRequest
import com.salazar.cheers.shared.data.request.LoginRequest
import com.salazar.cheers.shared.data.request.Passkey
import com.salazar.cheers.shared.data.response.BeginLoginResponse
import com.salazar.cheers.shared.data.response.BeginRegistrationResponse
import com.salazar.cheers.shared.data.response.FinishLoginResponse
import com.salazar.cheers.shared.data.response.FinishRegistrationResponse
import com.salazar.cheers.shared.data.response.LoginResponse
import com.salazar.cheers.shared.util.Resource
import com.salazar.cheers.shared.util.result.DataError
import com.salazar.cheers.shared.util.result.RootError
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val bffApiService: BffApiService,
    private val credentialManager: CredentialManager,
    private val gson: Gson,
    private val authService: AuthServiceGrpcKt.AuthServiceCoroutineStub,
) {
    fun getIdToken(): com.salazar.cheers.shared.util.result.Result<String, DataError> {
        val user = auth.currentUser
            ?: return com.salazar.cheers.shared.util.result.Result.Error(DataError.Auth.NOT_SIGNED_IN)

        return try {
            val task: Task<GetTokenResult> = user.getIdToken(false)
            val tokenResult = Tasks.await(task)
            val idToken = tokenResult.token!!
            com.salazar.cheers.shared.util.result.Result.Success(idToken)
        } catch (e: Exception) {
            e.printStackTrace()
            com.salazar.cheers.shared.util.result.Result.Error(DataError.Network.UNKNOWN)
        }
    }

    fun isConnected(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.uid != null
    }

    fun checkIfAlreadySignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.uid != null
    }

    private fun getFirebaseCredentialFromIdToken(
        idToken: String?,
        accessToken: String? = null,
    ): AuthCredential {
        return GoogleAuthProvider.getCredential(idToken, accessToken)
    }

    suspend fun launchFidoFlow(
        activityContext: Context,
        request: CreatePasskeyRequest,
    ): Result<Passkey> {
        println(request)
        return try {
            val createCredentialRequest = CreatePublicKeyCredentialRequest(
                requestJson = gson.toJson(request),
                isAutoSelectAllowed = false,
            )
            // Launch the FIDO2 flow
            val response = credentialManager.createCredential(
                context = activityContext,
                request = createCredentialRequest,
            )
            val responseData = gson.fromJson(
                (response as CreatePublicKeyCredentialResponse).registrationResponseJson,
                CreatePasskeyResponseData::class.java
            )
            Result.success(responseData.toPasskey())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun parseGetPasskeyResponse(credential: Credential): GetPasskeyResponseData? {
        val json = (credential as PublicKeyCredential).authenticationResponseJson
        return gson.fromJson(json, GetPasskeyResponseData::class.java)
    }

    suspend fun signInWithCustomToken(token: String): Result<AuthResult> {
        return try {
            val response = auth.signInWithCustomToken(token).await()
            when {
                response.user != null -> Result.success(response)
                else -> Result.failure(Exception("Failed to sign in with custom token"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun signIn(idToken: String): Result<LoginResponse> {
        val request = LoginRequest(idToken = idToken)

        return try {
            val response = bffApiService.login(request = request)
            Result.success(response)
        } catch (e: HttpException) {
            e.printStackTrace()
            if (e.code() == 404) {
                return Result.failure(NotRegisteredException(idToken))
            }
            Result.failure(e)
        }
    }

    class NotRegisteredException(idToken: String) : Exception(idToken)

    suspend fun signInWithFirebaseIdToken(
        idToken: String?,
    ): AuthResult? {
        val credential = getFirebaseCredentialFromIdToken(idToken = idToken)
        return signInWithCredential(credential = credential)
    }

    suspend fun signInWithGoogle(
        idToken: String,
    ): Resource<Unit> {
        try {
            val credential = getFirebaseCredentialFromIdToken(idToken = idToken)
            signInWithCredential(credential)
        } catch (e: ApiException) {
            e.printStackTrace()
        }
        return Resource.Success(Unit)
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthResult? {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    private suspend fun signInWithCredential(credential: AuthCredential): AuthResult? {
        return auth.signInWithCredential(credential).await()
    }

    fun signOut(): com.salazar.cheers.shared.util.result.Result<Unit, RootError> {
        FirebaseAuth.getInstance().signOut()
        return com.salazar.cheers.shared.util.result.Result.Success(Unit)
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            FirebaseAuth.getInstance().currentUser!!.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun sendSignInLink(email: String): Task<Void> {
        val actionCodeSettings = actionCodeSettings {
            url = "https://cheers-a275e.web.app/signIn"
            handleCodeInApp = true
            setIOSBundleId("com.salazar.cheers")
            setAndroidPackageName(
                "com.salazar.cheers",
                true,
                "12"
            )
        }
        return Firebase.auth.sendSignInLinkToEmail(email, actionCodeSettings)
    }

    fun updatePassword(
        password: String,
    ) = flow<Resource<Unit>> {
        emit(Resource.Loading(true))
        try {
            Firebase.auth.currentUser!!.updatePassword(password).await()
            Firebase.auth.currentUser?.reload()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("AUTH", "Failed updating password $e")
            when (e) {
                is FirebaseAuthRecentLoginRequiredException -> {}
                is FirebaseAuthWeakPasswordException -> {}
                is FirebaseAuthInvalidUserException -> {}
            }
            emit(Resource.Error(e.message.toString()))
        }
        emit(Resource.Loading(false))
    }

    suspend fun listPasskeys(): com.salazar.cheers.shared.util.result.Result<List<com.salazar.cheers.core.model.Credential>, DataError> {
        return try {
            val response = authService.listCredentials(
                request = ListCredentialsRequest.newBuilder().build()
            )
            val credentials = response.credentialsList.map {
                it.toCredential()
            }
            com.salazar.cheers.shared.util.result.Result.Success(credentials)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            com.salazar.cheers.shared.util.result.Result.Error(DataError.Network.UNKNOWN)
        }
    }

    suspend fun beginLogin(
        username: String?,
    ): Result<BeginLoginResponse?> {
        if (username == null)
            return Result.success(null)

        return try {
            val response = bffApiService.beginLogin(
                username = username
            )
            Result.success(response)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun finishLogin(
        challenge: String,
        username: String,
        passkey: FinishLoginPasskey,
    ): Result<FinishLoginResponse> {
        return try {
            val response = bffApiService.finishLogin(
                FinishLoginRequest(
                    username = username,
                    passkey = passkey,
                    challenge = challenge,
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            Result.failure(e)
        }
    }


    suspend fun beginRegistration(
        username: String,
    ): Result<BeginRegistrationResponse> {
        return try {
            val response = bffApiService.beginRegistration(
                username = username
            )
            Result.success(response)
        } catch (e: HttpException) {
            e.printStackTrace()
            if (e.code() == 409)
                return Result.failure(Exception("Username already taken."))
            Result.failure(e)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun finishRegistration(
        challenge: String,
        userId: String,
        username: String,
        passkey: Passkey,
    ): Result<FinishRegistrationResponse> {
        return try {
            val request = FinishRegistrationRequest(
                email = "",
                username = username,
                passkey = passkey,
                challenge = challenge,
                userId = userId,
                device = Device(
                    name = "${Build.MANUFACTURER} ${Build.MODEL}",
                    model = Build.MODEL,
                ),
            )
            val response = bffApiService.finishRegistration(request = request)
            Result.success(response)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun showSigninOptions(
        showGoogleOptions: Boolean,
        activityContext: Context,
        beginLoginResponse: BeginLoginResponse?,
    ): Result<GetCredentialResponse> {
        val credentialManager = CredentialManager.create(activityContext)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            // When 'true' -> Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(activityContext.getString(R.string.default_web_client_id))
            .build()

        var request = GetCredentialRequest.Builder()

        if (showGoogleOptions) {
            request = request.addCredentialOption(googleIdOption)
        }

        if (beginLoginResponse != null) {
            // Get passkey from the user's public key credential provider.
            val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                requestJson = gson.toJson(beginLoginResponse.toGetPasskeyRequest())
            )
            request = request.addCredentialOption(getPublicKeyCredentialOption)
        }

        return try {
            val result = credentialManager.getCredential(
                context = activityContext,
                request = request.build(),
            )
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

