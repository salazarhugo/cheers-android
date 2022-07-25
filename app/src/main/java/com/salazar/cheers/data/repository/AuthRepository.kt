package com.salazar.cheers.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.internal.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val service: CoreService,
    private val userDao: UserDao,
    private val auth: FirebaseAuth,
) {


    fun isUserAuthenticatedInFirebase() = auth.currentUser != null

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Result<Boolean>> = flow {
        try {
//            emit(Result.Loading)
            auth.signInWithEmailAndPassword(email, password).await()
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun getUser(): Result<User?> {
        if (auth.currentUser == null)
            return Result.Success(null)

        val userId = auth.currentUser?.uid!!
        val user = userDao.getUserNullable(userIdOrUsername = userId)
        if (user != null)
            return Result.Success(user)

        return try {
            val user = service.getUser(userIdOrUsername = userId)
            Result.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
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

    fun reAuthenticate(email: String) {
//        val user = Firebase.auth.currentUser!!
//        sendSignInLink(email = email)
//
//        val credential = EmailAuthProvider
//            .getCredentialWithLink("user@example.com", )
//
//        user.reauthenticate(credential)
//            .addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }
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

