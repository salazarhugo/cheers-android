package com.salazar.cheers.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.salazar.cheers.data.auth.mapper.toCreatePasskeyRequest
import com.salazar.cheers.data.auth.mapper.toGetPasskeyRequest
import com.salazar.cheers.data.auth.mapper.toPasskey
import com.salazar.cheers.data.auth.model.CreatePasskeyRequest
import com.salazar.cheers.data.auth.model.CreatePasskeyResponseData
import com.salazar.cheers.data.auth.model.GetPasskeyRequest
import com.salazar.cheers.data.auth.model.GetPasskeyResponseData
import com.salazar.cheers.shared.data.request.FinishRegistrationRequest
import com.salazar.cheers.shared.data.BffApiService
import com.salazar.cheers.shared.data.request.FinishLoginPasskey
import com.salazar.cheers.shared.data.request.FinishLoginRequest
import com.salazar.cheers.shared.data.request.LoginRequest
import com.salazar.cheers.shared.data.request.Passkey
import com.salazar.cheers.shared.data.response.BeginLoginResponse
import com.salazar.cheers.shared.data.response.BeginRegistrationResponse
import com.salazar.cheers.shared.data.response.FinishLoginResponse
import com.salazar.cheers.shared.data.response.LoginResponse
import com.salazar.common.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val bffApiService: BffApiService,
    private val credentialManager: CredentialManager,
    private val gson: Gson,
) {
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
        return try {
            val createCredentialRequest = CreatePublicKeyCredentialRequest(gson.toJson(request))
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
            Result.failure(e)
        }
    }

    suspend fun parseGetPasskeyResponse(credential: Credential): GetPasskeyResponseData? {
        val json = (credential as PublicKeyCredential).authenticationResponseJson
        return gson.fromJson(json, GetPasskeyResponseData::class.java)
    }

    suspend fun signInWithCustomToken(token: String): Result<Unit> {
        return try {
            val response = auth.signInWithCustomToken(token).await()
            when {
                response.user != null -> Result.success(Unit)
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
                return Result.failure(NotRegisteredException(""))
            }
            Result.failure(e)
        }
    }
    class NotRegisteredException(message: String) : Exception(message)

    suspend fun signInWithOneTap(
        idToken: String?,
    ): AuthResult? {
        val credential = getFirebaseCredentialFromIdToken(idToken)
        return signInWithCredential(credential)
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

    suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
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
    )  = flow<Resource<Unit>> {
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

    suspend fun beginLogin(
        username: String,
    ): Result<BeginLoginResponse> {
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
        }
        catch (e: HttpException) {
            e.printStackTrace()
            if (e.code() == 409)
                return Result.failure(Exception("Username already taken."))
            Result.failure(e)
        }
        catch (e: Exception) {
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
     ): Result<Unit> {
        return try {
            val response = bffApiService.finishRegistration(
                FinishRegistrationRequest(
                    email = "",
                    username = username,
                    passkey = passkey,
                    challenge = challenge,
                    userId = userId,
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun showSigninOptions(
        activityContext: Context,
        beginLoginResponse: BeginLoginResponse,
    ): Result<GetCredentialResponse> {
        val credentialManager = CredentialManager.create(activityContext)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(activityContext.getString(R.string.default_web_client_id))
            .build()

        // Get passkey from the user's public key credential provider.
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = gson.toJson(beginLoginResponse.toGetPasskeyRequest())
        )

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .addCredentialOption(getPublicKeyCredentialOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                context = activityContext,
                request = request,
            )
            Result.success(result)
        } catch (e : GetCredentialException) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

