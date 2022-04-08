package com.salazar.cheers.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.Neo4jService
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
    private val service: Neo4jService,
    private val userDao: UserDao,
    private val auth: FirebaseAuth,
) {

    fun isUserAuthenticatedInFirebase() = auth.currentUser != null

     suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<Result<Boolean>> = flow {
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
        val user = userDao.getUserNullable(userId = userId)
        if (user != null)
            return Result.Success(user)

        return service.getUser(userIdOrUsername = userId)
    }

    fun getUserAuthState() = callbackFlow  {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}

