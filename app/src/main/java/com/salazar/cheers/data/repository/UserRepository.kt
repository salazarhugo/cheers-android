package com.salazar.cheers.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.PostDao
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.db.UserStatsDao
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: Neo4jService,
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val postDao: PostDao,
) {

    suspend fun blockUser(userId: String) = withContext(Dispatchers.IO) {
        postDao.deleteWithAuthorId(authorId = userId)
        Neo4jUtil.blockUser(otherUserId = userId)
    }

    suspend fun getUserStats(username: String) = withContext(Dispatchers.IO) {
        when (val result = service.getUserStats(username = username)) {
            is Result.Success -> userStatsDao.insert(userStats = result.data)
            is Result.Error -> {}
        }
        return@withContext userStatsDao.getUserStats(username)
    }

    suspend fun queryUsers(query: String) = withContext(Dispatchers.IO) {
        userDao.insertAll(service.queryUsers(query = query))
        return@withContext userDao.queryUsers(query = query)
    }

    suspend fun toggleFollow(user: User) {
        userDao.update(user.copy(isFollowed = !user.isFollowed))

        if (user.isFollowed)
            service.unfollowUser(user.username)
        else
            service.followUser(user.username)
    }

    suspend fun getCurrentUser(): User = withContext(Dispatchers.IO) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        return@withContext getUser(userIdOrUsername = currentUserId)
    }

    suspend fun getUsersWithListOfIds(ids: List<String>): List<User> = withContext(Dispatchers.IO) {
        ids.forEach { id ->
            refreshUser(userIdOrUsername = id)
        }
        return@withContext userDao.getUsersWithListOfIds(ids = ids)
    }

    suspend fun getUser(userIdOrUsername: String): User = withContext(Dispatchers.IO) {
        refreshUser(userIdOrUsername = userIdOrUsername)
        return@withContext userDao.getUserWithUsername(userIdOrUsername = userIdOrUsername)
    }

    private suspend fun refreshUser(userIdOrUsername: String) {
        when (val result = service.getUser(userIdOrUsername = userIdOrUsername)) {
            is Result.Success -> userDao.insert(result.data)
            is Result.Error -> {
                Log.e("User Service", result.exception.message.toString())
            }
        }
    }

    suspend fun getSuggestions(): List<User> = withContext(Dispatchers.IO) {
        when (val result = service.getSuggestions()) {
            is Result.Success -> {
                val suggestions = result.data
                userDao.insertAll(suggestions)
                val ids = suggestions.map { it.id }
                return@withContext userDao.getUsersWithListOfIds(ids)
            }
            is Result.Error -> {
                Log.e("User Service", result.exception.message.toString())
                return@withContext emptyList()
            }
        }
    }
}