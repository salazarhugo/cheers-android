package com.salazar.cheers.data.auth

import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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
import com.salazar.cheers.shared.data.BffApiService
import com.salazar.cheers.shared.data.request.LoginRequest
import com.salazar.cheers.shared.data.response.LoginResponse
import com.salazar.common.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val bffApiService: BffApiService,
    private val auth: FirebaseAuth,
) {
    fun checkIfAlreadySignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.uid != null
    }

    private fun getFirebaseCredentialFromIdToken(
        idToken: String?,
        accessToken: String? = null,
    ): AuthCredential {
        return GoogleAuthProvider.getCredential(idToken, accessToken)
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

    suspend fun deleteAccount(): Task<Void> {
        return FirebaseAuth.getInstance().currentUser!!.delete()
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
    )  = flow {
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

    fun getUserAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    fun getUserIdToken() = callbackFlow {
        val authStateListener = FirebaseAuth.IdTokenListener { auth ->
            trySend(auth.currentUser)
        }

        auth.addIdTokenListener(authStateListener)

        awaitClose {
            auth.removeIdTokenListener(authStateListener)
        }
    }
}

