package com.salazar.cheers.data.user

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import cheers.friendship.v1.FriendshipServiceGrpcKt
import cheers.friendship.v1.ListFriendRequest
import cheers.notification.v1.CreateRegistrationTokenRequest
import cheers.notification.v1.NotificationServiceGrpcKt
import cheers.user.v1.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.user.workers.UploadProfilePicture
import com.salazar.common.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    application: Application,
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val userItemDao: UserItemDao,
    private val userService: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val notificationService: NotificationServiceGrpcKt.NotificationServiceCoroutineStub,
    private val service: FriendshipServiceGrpcKt.FriendshipServiceCoroutineStub,
) {
    private val workManager = WorkManager.getInstance(application)

    suspend fun listSuggestions(): Result<List<UserItem>> {
        return try {
            val request = ListSuggestionsRequest.newBuilder().build()

            val followers = userService.listSuggestions(request).usersList
            val userItems = followers.map { it.toUserItem() }
            userItemDao.insertAll(userItems)
            Result.success(userItems)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun checkUsername(
        username: String,
    ): Result<Boolean> {
        return try {
            val request = CheckUsernameRequest.newBuilder()
                .setUsername(username)
                .build()

            val response = userService.checkUsername(request = request)
            Result.success(response.valid)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun createUser(
        username: String,
        email: String,
    ): Result<User> {
        try {
            val request = CreateUserRequest.newBuilder()
                .setUsername(username)
                .setEmail(email)
                .build()

            val response = userService.createUser(request = request)
            val createTimeUser = response.user.toUser()

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
//            cheersDao.insertRecentUser(recentUser)
        } catch (e: Exception) {
            val user = userDao.getUserSuggestion(username = username) ?: return@withContext
//            cheersDao.insertRecentUser(recentUser)
        }
    }

    suspend fun blockUser(userId: String) = withContext(Dispatchers.IO) {
        try {
//            postDao.deleteWithAuthorId(authorId = userId)
            val request = BlockUserRequest.newBuilder()
                .setUserId(userId)
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

    fun listUserItems(): Flow<List<UserItem>> {
        return userItemDao.listUserItems()
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

    suspend fun getUserStats(username: String) = withContext(Dispatchers.IO) {
//        when (val result = service.getUserStats(username = username)) {
//            is Result.Success -> userStatsDao.insert(userStats = result.data)
//            is Result.Error -> {}
//
//        }
        return@withContext userStatsDao.getUserStats(username)
    }

    fun getRecentUsers(): Flow<List<RecentUser>> {
//        return cheersDao.getRecentUsers()
        return flow { }
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
            } catch (e: NullPointerException) {
                e.printStackTrace()
                emit(Resource.Error("Response is null"))
                null
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteUsers?.let { it ->
                val users = it.map { it.toUserItem() }
                userItemDao.insertAll(users)
                val local = userItemDao.searchUser(query = query)
                emit(Resource.Success(data = local))
            }
            emit(Resource.Loading(false))
        }
    }

    suspend fun updateLocalUserItem(userItem: com.salazar.cheers.core.model.UserItem) {
        userItemDao.update(userItem)
    }

    suspend fun updateLocalUser(user: User) {
        userDao.update(user)
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

//    suspend fun getActivity(fetchFromRemote: Boolean = false): Flow<Resource<List<Activity>?>> {
//        return flow {
//            emit(Resource.Loading(true))
//            val activity = userDao.getActivity()
//
//            if (activity.isNotEmpty()) {
//                emit(Resource.Success(activity))
//                emit(Resource.Loading(false))
//            }
//
//            if (!fetchFromRemote) {
//                emit(Resource.Loading(false))
//                return@flow
//            }
//
//            val remoteActivity = try {
////                coreService.getActivity()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                null
//            } catch (e: HttpException) {
//                e.printStackTrace()
//                null
//            }
//
//            remoteActivity?.let {
//                userDao.clearActivity()
////                userDao.insert(it.map { it.copy(accountId = Firebase.auth.currentUser?.uid!!) })
//                emit(Resource.Success(userDao.getActivity()))
//            }
//            emit(Resource.Loading(false))
//        }
//    }


    @Throws(Exception::class)
    suspend fun followUser(userID: String) {
        val request = FollowUserRequest.newBuilder()
            .setUserId(userID)
            .build()

        userService.followUser(request)
    }

    @Throws(Exception::class)
    suspend fun unfollowUser(userID: String) {
        val request = UnfollowUserRequest.newBuilder()
            .setUserId(userID)
            .build()

        userService.unfollowUser(request)
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

    fun getUsersIn(userIDs: List<String>): Flow<List<UserItem>> {
        return userItemDao.listUsersIn(userIDs)
    }

    suspend fun getCurrentUser(): User {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return userDao.getUser(userIdOrUsername = uid)
    }

    suspend fun getUserSignIn(userId: String): Flow<Resource<User>> {
        return flow {
            emit(Resource.Loading(true))
            val user = userDao.getUserNullable(userIdOrUsername = userId).firstOrNull()

            if (user != null) {
                emit(Resource.Success(user))
                emit(Resource.Loading(false))
            }

            val remoteUser = try {
                val request = GetUserRequest.newBuilder()
                    .setUserId(userId)
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

            if (remoteUser == null) {
                emit(Resource.Error("Failed to get user"))
            } else {
                userDao.insert(remoteUser.toUser())
                emit(Resource.Success(userDao.getUser(userId)))
            }
            emit(Resource.Loading(false))
        }
    }

    suspend fun listFriend(): Flow<Resource<List<UserItem>>> =
        flow {
            emit(Resource.Loading(true))

            val request = ListFriendRequest.newBuilder()
                .setUserId(FirebaseAuth.getInstance().currentUser?.uid)
                .build()

            val remoteFriendRequests = try {
                val response = service.listFriend(request = request)
                val users = response.itemsList.map {
                    it.toUserItem()
                }
                users
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Couldn't refresh friend requests"))
                null
            }

            remoteFriendRequests?.let {
                emit(Resource.Success(it))
            }

            emit(Resource.Loading(false))
        }

    fun getCurrentUserFlow(): Flow<User> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyFlow()
        return userDao.getUserFlow(userIdOrUsername = uid)
    }

    fun listUser(userIdOrUsername: String): Flow<User> {
        return userDao.getUserFlow(userIdOrUsername = userIdOrUsername)
    }

    fun getUserItem(userIdOrUsername: String): Flow<UserItem> {
        return userItemDao.getUserItem(userIdOrUsername = userIdOrUsername)
    }

    fun getUserFlow(userIdOrUsername: String): Flow<User> {
        return userDao.getUserFlow(userIdOrUsername = userIdOrUsername)
    }

    suspend fun fetchCurrentUser() = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext
        fetchUser(uid)
    }

    suspend fun fetchUser(
        userIDorUsername: String,
    ): Resource<Unit> {
        val request = GetUserRequest.newBuilder()
            .setUserId(userIDorUsername)
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
            userDao.clearSuggestions()
//            userDao.insertSuggestions(it.map { it.copy(accountId = uid) })
        }

        return@withContext userDao.getUserSuggestions()
    }

    suspend fun addTokenToNeo4j(newRegistrationToken: String?) {
        if (newRegistrationToken == null)
            throw NullPointerException("FCM token is null.")

        Log.i("GRPC", "ADDING TOKEN $newRegistrationToken")
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

    suspend fun uploadUserPicture(
        picture: Uri,
    ) {
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadProfilePicture>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "PHOTO_URI" to picture.toString(),
                    )
                )
            }
                .build()
        workManager.enqueue(uploadWorkRequest)
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