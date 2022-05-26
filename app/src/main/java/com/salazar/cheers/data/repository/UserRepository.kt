package com.salazar.cheers.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.backend.GoApi
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.db.PostDao
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.db.UserStatsDao
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val goApi: GoApi,
    private val service: Neo4jService,
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val postDao: PostDao,
    private val cheersDao: CheersDao,
) {

    suspend fun createUser(
        username: String,
        email: String,
    ): User? {
        try {
            val user = goApi.createUser(
                User().copy(
                    id = FirebaseAuth.getInstance().currentUser?.uid!!,
                    username = username,
                    email = email,
                )
            )
            userDao.insert(user)
            Log.d("YES", "Created User")
            return user
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("YES", e.toString())
            return null
        }
    }

    suspend fun blockUser(userId: String) = withContext(Dispatchers.IO) {
        try {
            postDao.deleteWithAuthorId(authorId = userId)
            goApi.blockUser(userId = userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getFollowersFollowing(userIdOrUsername: String) = withContext(Dispatchers.IO) {
        return@withContext service.getFollowersFollowing(userIdOrUsername = userIdOrUsername)
    }

    suspend fun getLocations(): FeatureCollection {
        return withContext(Dispatchers.IO) {
            try {
                val json = goApi.getLocations().string()
                return@withContext FeatureCollection.fromJson(json)
            } catch (e: HttpException) {
                Log.e("User Repository", e.toString())
            }
        } as FeatureCollection
    }

    suspend fun getUserStats(username: String) = withContext(Dispatchers.IO) {
        when (val result = service.getUserStats(username = username)) {
            is Result.Success -> userStatsDao.insert(userStats = result.data)
            is Result.Error -> {}

        }
        return@withContext userStatsDao.getUserStats(username)
    }

    fun getRecentUsers(): Flow<List<RecentUser>> {
        return cheersDao.getRecentUsers()
    }

    suspend fun queryUsers(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<User>>> {
        return flow {
            emit(Resource.Loading(true))
            val localUsers = userDao.searchUser(query = query)
            emit(
                Resource.Success(
                    data = localUsers
                )
            )

            val isDbEmpty = localUsers.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteUsers = try {
                goApi.searchUsers(query = query)
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: NullPointerException) {
                e.printStackTrace()
                emit(Resource.Error("Response is null"))
                null
            }

            remoteUsers?.let { users ->
                userDao.insertOrUpdateAll(users)
                emit(
                    Resource.Success(
                        data = users
                    )
                )
                emit(Resource.Loading(false))
            }
        }
    }

    suspend fun updateUser(username: String) {
        try {
            goApi.followUser(username = username)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun followUser(username: String) {
        try {
            goApi.followUser(username = username)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun unfollowUser(username: String) {
        try {
            goApi.unfollowUser(username = username)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            goApi.isUsernameAvailable(username = username)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun toggleFollow(user: User) {
        val newFollowersCount = if (user.isFollowed) user.followers - 1 else user.followers + 1
        val newUser = user.copy(isFollowed = !user.isFollowed, followers = newFollowersCount)

        userDao.update(newUser)

        if (user.isFollowed)
            unfollowUser(username = user.username)
        else
            followUser(username = user.username)
    }

    suspend fun getUsersWithListOfIds(ids: List<String>): List<User> = withContext(Dispatchers.IO) {
        return@withContext userDao.getUsersWithListOfIds(ids = ids)
    }

    suspend fun getCurrentUser(): User {
        return userDao.getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
    }

    fun getUserFlow(
        userIdOrUsername: String,
        fetchFromRemote: Boolean = false,
    ): Flow<User> {
        return flow {
            val user = userDao.getUserNullable(userIdOrUsername = userIdOrUsername)

            if (user != null)
                emit(user)

            if (user != null && !fetchFromRemote)
                return@flow

            val remoteUser = try {
                goApi.getUser(userIdOrUsername = userIdOrUsername)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            remoteUser?.let { user ->
                userDao.insertOrUpdate(user)
                emit(user)
            }
        }
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