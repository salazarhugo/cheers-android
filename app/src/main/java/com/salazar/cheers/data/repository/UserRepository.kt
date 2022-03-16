package com.salazar.cheers.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.PostDao
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: Neo4jService,
    private val userDao: UserDao,
    private val postDao: PostDao,
) {

    suspend fun getCurrentUser(): User = withContext(Dispatchers.IO) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        refreshUser(userIdOrUsername = currentUserId)
        return@withContext userDao.getUser(userId = currentUserId)
    }

    suspend fun blockUser(userId: String) = withContext(Dispatchers.IO) {
        postDao.deleteWithAuthorId(authorId = userId)
        Neo4jUtil.blockUser(otherUserId = userId)
    }

    suspend fun followUser(username: String) = withContext(Dispatchers.IO) {
        Neo4jUtil.followUser(username = username)
    }

    suspend fun unfollowUser(username: String) = withContext(Dispatchers.IO) {
        Neo4jUtil.unfollowUser(username = username)
    }

    suspend fun getUserWithUsername(username: String): User = withContext(Dispatchers.IO) {
        return@withContext userDao.getUserWithUsername(username = username)
    }

    suspend fun getUser(userId: String): User = withContext(Dispatchers.IO) {
        refreshUser(userIdOrUsername = userId)
        return@withContext userDao.getUser(userId = userId)
    }

    suspend fun refreshUser(userIdOrUsername: String) {
        val result = service.getUser(userIdOrUsername = userIdOrUsername)
        when (result) {
            is Result.Success -> userDao.insert(result.data)
            is Result.Error ->  {
                Log.e("User Service", result.exception.message.toString())
            }
        }
    }
}