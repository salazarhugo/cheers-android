package com.salazar.cheers.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.PostDao
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.db.UserStatsDao
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
        service.blockUser(otherUserId = userId)
    }

    suspend fun getFollowersFollowing(userIdOrUsername: String) = withContext(Dispatchers.IO) {
        return@withContext service.getFollowersFollowing(userIdOrUsername = userIdOrUsername)
    }

    suspend fun getUserStats(username: String) = withContext(Dispatchers.IO) {
        when (val result = service.getUserStats(username = username)) {
            is Result.Success -> userStatsDao.insert(userStats = result.data)
            is Result.Error -> {}
        }
        return@withContext userStatsDao.getUserStats(username)
    }

    suspend fun refreshQueryUsers(query: String) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateAll(service.queryUsers(query = query))
    }

    suspend fun queryUsers(query: String) = userDao.queryUsers(query = query)

    suspend fun toggleFollow(user: User) {
        val newFollowersCount = if (user.isFollowed) user.followers - 1 else user.followers + 1
        val newUser = user.copy(isFollowed = !user.isFollowed, followers = newFollowersCount)

        userDao.update(newUser)

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
        launch(Dispatchers.IO) {
            ids.forEach { id ->
                refreshUser(userIdOrUsername = id)
            }
        }
        return@withContext userDao.getUsersWithListOfIds(ids = ids)
    }

    fun getUserFlow(userIdOrUsername: String): Flow<User> =
        userDao.getUserFlowWithUsername(userIdOrUsername = userIdOrUsername)

    suspend fun getUser(userIdOrUsername: String): User = withContext(Dispatchers.IO) {
        launch(Dispatchers.IO) {
            refreshUser(userIdOrUsername = userIdOrUsername)
        }
        return@withContext userDao.getUserWithUsername(userIdOrUsername = userIdOrUsername)
    }

    suspend fun refreshUser(userIdOrUsername: String): Result<User?> {
        val result = service.getUser(userIdOrUsername = userIdOrUsername)
        when (result) {
            is Result.Success -> {
                if (result.data != null)
                    userDao.insertOrUpdate(result.data)
            }
            is Result.Error -> {}
        }
        return result
    }

    suspend fun getSuggestions(): List<User> = withContext(Dispatchers.IO) {
        when (val result = service.getSuggestions()) {
            is Result.Success -> {
                val suggestions = result.data
                userDao.insertOrUpdateAll(suggestions)
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