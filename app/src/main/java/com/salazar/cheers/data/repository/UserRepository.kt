package com.salazar.cheers.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: Neo4jService,
    private val userDao: UserDao
) {

    suspend fun getCurrentUser(): User = withContext(Dispatchers.IO) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        refreshUser(userId = currentUserId)
        return@withContext userDao.getUser(userId = currentUserId)
    }

    suspend fun getUser(userId: String): User = withContext(Dispatchers.IO) {
        refreshUser(userId = userId)
        return@withContext userDao.getUser(userId = userId)
    }

    private suspend fun refreshUser(userId: String) {
        val result = service.getUser(userId = userId)
        when (result) {
            is Result.Success -> userDao.insert(result.data)
            is Result.Error -> Result.Error(result.exception)
        }
    }
}