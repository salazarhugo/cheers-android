package com.salazar.cheers.data.repository

import android.util.Log
import cheers.notification.v1.CreateRegistrationTokenRequest
import cheers.notification.v1.NotificationServiceGrpcKt
import cheers.user.v1.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.*
import com.salazar.cheers.data.db.entities.RecentUser
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.mapper.toUser
import com.salazar.cheers.data.mapper.toUserItem
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val postDao: PostDao,
    private val cheersDao: CheersDao,
    private val userItemDao: UserItemDao,
    private val userService: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val notificationService: NotificationServiceGrpcKt.NotificationServiceCoroutineStub,
) {

    suspend fun createUser(
        username: String,
        email: String,
    ): Result<User> {
        try {
            val authUser = FirebaseAuth.getInstance().currentUser!!

            val user = cheers.type.UserOuterClass.User.newBuilder()
                .setUsername(username)
                .setEmail(authUser.email ?: email)
                .setName(authUser.displayName ?: "")
                .setPhoneNumber(authUser.phoneNumber ?: "")
                .build()

            val request = CreateUserRequest.newBuilder()
                .setUser(user)
                .build()

            val response = userService.createUser(request = request)
            val createTimeUser = response.toUser()

            userDao.insert(createTimeUser)

            return Result.success(createTimeUser)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    suspend fun insertRecent(username: String) = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUser(userIdOrUsername = username)
            val recentUser = RecentUser(
                id = user.id,
                fullName = user.name,
                username = user.username,
                profilePictureUrl = user.picture,
                verified = user.verified,
                date = Instant.now().epochSecond
            )
            cheersDao.insertRecentUser(recentUser)
        } catch (e: Exception) {
            val user = userDao.getUserSuggestion(username = username) ?: return@withContext
            val recentUser = RecentUser(
                id = user.id,
                fullName = user.name,
                username = user.username,
                profilePictureUrl = user.picture,
                verified = user.verified,
                date = Instant.now().epochSecond
            )
            cheersDao.insertRecentUser(recentUser)
        }
    }

    suspend fun blockUser(userId: String) = withContext(Dispatchers.IO) {
        try {
            postDao.deleteWithAuthorId(authorId = userId)
            val request = BlockUserRequest.newBuilder()
                .setId(userId)
                .build()

            userService.blockUser(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getFollowers(userIdOrUsername: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = ListFollowersRequest.newBuilder()
                .setUserId(userIdOrUsername)
                .build()

            val followers = userService.listFollowers(request).usersList
            val userItems = followers.map { it.toUserItem() }
            userItemDao.insertAll(userItems)
            userItems
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getFollowing(userIdOrUsername: String) = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = ListFollowingRequest.newBuilder()
                .setUserId(userIdOrUsername)
                .build()

            val followers = userService.listFollowing(request).usersList
            val userItems = followers.map { it.toUserItem() }
            userItemDao.insertAll(userItems)
            userItems
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLocations(): FeatureCollection? {
        return withContext(Dispatchers.IO) {
            try {
//                val json = coreService.getLocations().string()
//                return@withContext FeatureCollection.fromJson(json)
                null
            } catch (e: HttpException) {
                Log.e("User Repository", e.toString())
                null
            }
        }
    }

    suspend fun getUserStats(username: String) = withContext(Dispatchers.IO) {
//        when (val result = service.getUserStats(username = username)) {
//            is Result.Success -> userStatsDao.insert(userStats = result.data)
//            is Result.Error -> {}
//
//        }
        return@withContext userStatsDao.getUserStats(username)
    }

    fun getRecentUsers(): Flow<List<RecentUser>> {
        return cheersDao.getRecentUsers()
    }

    suspend fun queryUsers(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<UserItem>>> {
        return flow {
            emit(Resource.Loading(true))
            val localUsers = userItemDao.searchUser(query = query)
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

            val request = SearchUserRequest.newBuilder()
                .setQuery(query)
                .build()

            val remoteUsers = try {
                userService.searchUser(request).usersList
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

            remoteUsers?.let { it ->
                val users = it.map { it.toUserItem() }
                userItemDao.insertAll(users)
                emit(Resource.Success(data = users))
            }
            emit(Resource.Loading(false))
        }
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        try {
            userDao.update(user)

            val request = UpdateUserRequest.newBuilder()
                .setPicture(user.picture)
                .setBio(user.bio)
                .setName(user.name)
                .setWebsite(user.website)
                .build()

            val response = userService.updateUser(request)
            userDao.insert(response.toUser())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getActivity(fetchFromRemote: Boolean = false): Flow<Resource<List<Activity>?>> {
        return flow {
            emit(Resource.Loading(true))
            val activity = userDao.getActivity()

            if (activity.isNotEmpty()) {
                emit(Resource.Success(activity))
                emit(Resource.Loading(false))
            }

            if (!fetchFromRemote) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteActivity = try {
//                coreService.getActivity()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                null
            }

            remoteActivity?.let {
                userDao.clearActivity()
//                userDao.insert(it.map { it.copy(accountId = Firebase.auth.currentUser?.uid!!) })
                emit(Resource.Success(userDao.getActivity()))
            }
            emit(Resource.Loading(false))
        }
    }


    @Throws(Exception::class)
    suspend fun followUser(userID: String) {
        val request = FollowUserRequest.newBuilder()
            .setId(userID)
            .build()

        userService.followUser(request)
    }

    @Throws(Exception::class)
    suspend fun unfollowUser(userID: String) {
        val request = UnfollowUserRequest.newBuilder()
            .setId(userID)
            .build()

        userService.unfollowUser(request)
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        return try {
//            publicService.isUsernameAvailable(username = username)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun toggleFollow(userID: String) {
        try {
            val user = userDao.getUser(userID)

            val newFollowersCount = if (user.followBack) user.followers - 1 else user.followers + 1
            val newUser = user.copy(followBack = !user.followBack, followers = newFollowersCount)

            userDao.update(newUser)

            if (user.followBack)
                unfollowUser(userID = user.id)
            else
                followUser(userID = user.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

            if (user != null) {
                emit(Resource.Success(user))
                emit(Resource.Loading(false))
            }

            val remoteUser = try {
                val request = GetUserRequest.newBuilder()
                    .setId(userId)
                    .build()
                userService.getUser(request)
            } catch (e: NullPointerException) {
                e.printStackTrace()
                emit(Resource.Error("User not found"))
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            remoteUser?.let { safeRemoteUser ->
                userDao.insert(safeRemoteUser.toUser())
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
        userIDorUsername: String,
    ): Resource<Unit> {
        val request = GetUserRequest.newBuilder()
            .setId(userIDorUsername)
            .build()

        return try {
            val remoteUser = userService.getUser(request).toUser()
            userDao.insert(remoteUser)
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Oups, something went wrong. Please try again later.")
        }
    }

    suspend fun getSuggestions() = withContext(Dispatchers.IO) {
        val suggestions = try {
//            coreService.suggestions()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        suggestions?.let {
            val uid = FirebaseAuth.getInstance().currentUser?.uid!!
            userDao.clearSuggestions()
//            userDao.insertSuggestions(it.map { it.copy(accountId = uid) })
        }

        return@withContext userDao.getUserSuggestions()
    }

    suspend fun addTokenToNeo4j(newRegistrationToken: String?) {
        if (newRegistrationToken == null)
            throw NullPointerException("FCM token is null.")

        Log.e("GRPC", "ADDING TOKEN")
        try {
            val request = CreateRegistrationTokenRequest.newBuilder()
                .setToken(newRegistrationToken)
                .build()

            notificationService.createRegistrationToken(request = request)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("GRPC", e.toString())
        }
    }

    suspend fun saveUserPicture(
        picture: String,
    ) {
//        val file = File(context.cacheDir, picture)
//        if (file.ex)
//
//        try {
//            val response = service.downloadSeafarerProfilePicture(
//                sourceId = seafarerId,
//                attachmentId = attachmentId,
//            )
//
//            response.body()?.let { body ->
//                withContext(Dispatchers.IO) {
//                    val outputStream = FileOutputStream(file)
//                    outputStream.use { stream ->
//                        try {
//                            stream.write(body.bytes())
//                        } catch (e: IOException) {
//                        }
//                    }
//                    file.absolutePath
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }
}