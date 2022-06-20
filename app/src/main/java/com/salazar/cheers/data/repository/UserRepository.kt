package com.salazar.cheers.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.backend.PublicService
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.db.PostDao
import com.salazar.cheers.data.db.UserDao
import com.salazar.cheers.data.db.UserStatsDao
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val coreService: CoreService,
    private val publicService: PublicService,
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
            val authUser = FirebaseAuth.getInstance().currentUser!!
            val user = coreService.createUser(
                User().copy(
                    username = username,
                    email = authUser.email ?: email,
                    name = authUser.displayName ?: "",
                    phoneNumber = authUser.phoneNumber ?: "",
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

    suspend fun insertRecent(username: String) = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUser(userIdOrUsername = username)
            val recentUser = RecentUser(
                id = user.id,
                fullName = user.name,
                username = user.username,
                profilePictureUrl = user.profilePictureUrl,
                verified = user.verified,
                date = Instant.now().epochSecond
            )
            cheersDao.insertRecentUser(recentUser)
        }catch (e: Exception) {
            val user = userDao.getUserSuggestion(username = username) ?: return@withContext
            val recentUser = RecentUser(
                id = user.id,
                fullName = user.name,
                username = user.username,
                profilePictureUrl = user.avatar,
                verified = user.verified,
                date = Instant.now().epochSecond
            )
            cheersDao.insertRecentUser(recentUser)
        }
    }

    suspend fun blockUser(userId: String) = withContext(Dispatchers.IO) {
        try {
            postDao.deleteWithAuthorId(authorId = userId)
            coreService.blockUser(userId = userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getFollowers(userIdOrUsername: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            coreService.followersList(userIdOrUsername = userIdOrUsername)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getFollowing(userIdOrUsername: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            coreService.followingList(userIdOrUsername = userIdOrUsername)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLocations(): FeatureCollection {
        return withContext(Dispatchers.IO) {
            try {
                val json = coreService.getLocations().string()
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
                coreService.searchUsers(query = query)
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
                userDao.insertAll(users)
                emit(
                    Resource.Success(
                        data = users
                    )
                )
                emit(Resource.Loading(false))
            }
        }
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        try {
            userDao.update(user)
            coreService.updateUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getActivity(fetchFromRemote: Boolean = false): Flow<List<Activity>?> {
        return flow {
            val activity = userDao.getActivity()
            emit(activity)

            if (!fetchFromRemote)
                return@flow

            val remoteActivity = try {
                coreService.getActivity()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                null
            }

            remoteActivity?.let {
                userDao.clearActivity()
                userDao.insert(it.map { it.copy(accountId = Firebase.auth.currentUser?.uid!!) })
                emit(userDao.getActivity())
            }
        }
    }

    suspend fun followUser(username: String) {
        try {
            coreService.followUser(username = username)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun unfollowUser(username: String) {
        try {
            coreService.unfollowUser(username = username)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
            publicService.isUsernameAvailable(username = username)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun toggleFollow(username: String) {
        val user = userDao.getUserSuggestion(username) ?: return

        val newUser = user.copy(followBack = !user.followBack)
        userDao.update(newUser)

        if (user.followBack)
            unfollowUser(username = user.username)
        else
            followUser(username = user.username)
    }

    suspend fun toggleFollow(user: User) {
        val newFollowersCount = if (user.followBack) user.followers - 1 else user.followers + 1
        val newUser = user.copy(followBack = !user.followBack, followers = newFollowersCount)

        userDao.update(newUser)

        if (user.followBack)
            unfollowUser(username = user.username)
        else
            followUser(username = user.username)
    }

    suspend fun getUsersWithListOfIds(ids: List<String>): List<User> = withContext(Dispatchers.IO) {
        return@withContext userDao.getUsersWithListOfIds(ids = ids)
    }

    suspend fun getCurrentUserNullable(): User? {
        return userDao.getUserNullable(FirebaseAuth.getInstance().currentUser?.uid!!)
    }

    suspend fun getCurrentUser(): User {
        return userDao.getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
    }

    suspend fun getUserSignIn(userId: String): Flow<Resource<User>> {
        return flow {
            emit(Resource.Loading(true))
            val user = userDao.getUserNullable(userIdOrUsername = userId)

            if (user != null)
                emit(Resource.Success(user))

            val remoteUser = try {
                coreService.getUser(userIdOrUsername = userId)
            } catch (e: NullPointerException) {
                e.printStackTrace()
                emit(Resource.Error(""))
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            remoteUser?.let { safeRemoteUser ->
                userDao.insert(safeRemoteUser)
                emit(Resource.Success(userDao.getUser(userId)))
            }
            emit(Resource.Loading(false))
        }
    }

    fun getCurrentUserFlow(): Flow<User> {
        return userDao.getUserFlow(userIdOrUsername = Firebase.auth.currentUser?.uid!!)
    }

    fun getUserFlow(userIdOrUsername: String): Flow<User> {
        return userDao.getUserFlow(userIdOrUsername = userIdOrUsername)
    }

    suspend fun fetchUser(
        userIdOrUsername: String,
    ) {
        val remoteUser = try {
            coreService.getUser(userIdOrUsername = userIdOrUsername)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        remoteUser?.let { user ->
            userDao.insert(user)
        }
    }

    suspend fun getSuggestions() = withContext(Dispatchers.IO) {

        val suggestions = try {
            coreService.suggestions()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        suggestions?.let {
            val uid = FirebaseAuth.getInstance().currentUser?.uid!!
            userDao.clearSuggestions()
            userDao.insertSuggestions(it.map { it.copy(accountId = uid) })
        }

        return@withContext userDao.getUserSuggestions()
    }

    suspend fun addTokenToNeo4j(newRegistrationToken: String?) {
        if (newRegistrationToken == null)
            throw NullPointerException("FCM token is null.")

        try {
            coreService.addRegistrationToken(newRegistrationToken)
        } catch (e: HttpException) {
            e.printStackTrace()
        }
    }
}