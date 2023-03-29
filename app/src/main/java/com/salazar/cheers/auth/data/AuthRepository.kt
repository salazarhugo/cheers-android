package com.salazar.cheers.auth.data

import android.util.Log
import cheers.user.v1.GetUserRequest
import cheers.user.v1.UserServiceGrpcKt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.CheersDatabase
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.mapper.toUser
import com.salazar.cheers.internal.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val database: CheersDatabase,
    private val userDao: UserDao,
    private val auth: FirebaseAuth,
    private val userService: UserServiceGrpcKt.UserServiceCoroutineStub,
) {
    suspend fun getUser(): Result<User?> {
        if (auth.currentUser == null)
            return Result.Success(null)

        val userId = auth.currentUser?.uid!!
        val user = userDao.getUserNullable(userIdOrUsername = userId).firstOrNull()
        if (user != null)
            return Result.Success(user)

        val request = GetUserRequest.newBuilder()
            .setUserId(userId)
            .build()

        return try {
            val user = userService.getUser(request).user.toUser()
            Result.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Failed to get user")
        }
    }

    suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
        database.clearAllTables()
    }

    fun sendSignInLink(email: String): com.google.android.gms.tasks.Task<Void> {
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

