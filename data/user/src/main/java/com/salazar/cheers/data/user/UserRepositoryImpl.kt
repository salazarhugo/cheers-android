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
import cheers.user.v1.BlockUserRequest
import cheers.user.v1.CheckUsernameRequest
import cheers.user.v1.CreateUserRequest
import cheers.user.v1.GetUserRequest
import cheers.user.v1.ListFollowersRequest
import cheers.user.v1.ListFollowingRequest
import cheers.user.v1.ListSuggestionsRequest
import cheers.user.v1.SearchUserRequest
import cheers.user.v1.UpdateUserRequest
import cheers.user.v1.UserServiceGrpcKt
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.db.dao.UserDao
import com.salazar.cheers.core.db.dao.UserItemDao
import com.salazar.cheers.core.db.dao.UserStatsDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.CheckUsernameResult
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.UserID
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.UserStats
import com.salazar.cheers.core.model.UserSuggestion
import com.salazar.cheers.data.user.workers.UploadProfileBanner
import com.salazar.cheers.data.user.workers.UploadProfilePicture
import com.salazar.cheers.shared.data.mapper.toCheckUsernameResult
import com.salazar.cheers.shared.data.mapper.toUser
import com.salazar.cheers.shared.data.mapper.toUserItem
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    application: Application,
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val userItemDao: UserItemDao,
    private val userService: UserServiceGrpcKt.UserServiceCoroutineStub,
    private val notificationService: NotificationServiceGrpcKt.NotificationServiceCoroutineStub,
    private val service: FriendshipServiceGrpcKt.FriendshipServiceCoroutineStub,
) : UserRepository {
    private val workManager = WorkManager.getInstance(application)

    override suspend fun listSuggestions(): Result<List<UserItem>> {
        return try {
            val request = ListSuggestionsRequest.newBuilder().build()

            val followers = userService.listSuggestions(request).usersList
            val userItems = followers.map { it.toUserItem() }
            userItemDao.insertAll(userItems.asEntity())
            Result.success(userItems)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun checkUsername(
        username: String,
    ): Result<CheckUsernameResult> {
        return try {
            val request = CheckUsernameRequest.newBuilder()
                .setUsername(username)
                .build()

            val response = userService.checkUsername(request = request)
            Result.success(response.toCheckUsernameResult())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun createUser(
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

            userDao.insert(createTimeUser.asEntity())

            return Result.success(createTimeUser)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    override suspend fun blockUser(userId: String) {
        return withContext(Dispatchers.IO) {
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
    }

    override suspend fun getFollowers(userIdOrUsername: String): List<UserItem>? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val request = ListFollowersRequest.newBuilder()
                    .setUserId(userIdOrUsername)
                    .build()

                val followers = userService.listFollowers(request).usersList
                val userItems = followers.map { it.toUserItem() }
                userItemDao.insertAll(userItems.asEntity())
                userItems
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun listUserItems(): Flow<List<UserItem>> {
        return userItemDao.listUserItems().map { it.asExternalModel() }
    }

    override suspend fun getFollowing(userIdOrUsername: String): List<UserItem>? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val request = ListFollowingRequest.newBuilder()
                    .setUserId(userIdOrUsername)
                    .build()

                val followers = userService.listFollowing(request).usersList
                val userItems = followers.map { it.toUserItem() }
                userItemDao.insertAll(userItems.asEntity())
                userItems
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun getUserStats(username: String): UserStats {
        return withContext(Dispatchers.IO) {
            //        when (val result = service.getUserStats(username = username)) {
            //            is Result.Success -> userStatsDao.insert(userStats = result.data)
            //            is Result.Error -> {}
            //
            //        }
            return@withContext userStatsDao.getUserStats(username).asExternalModel()
        }
    }

    override suspend fun queryUsers(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<UserItem>>> {
        return flow {
            emit(Resource.Loading(true))
            val localUsers = userItemDao.searchUser(query = query)
            emit(
                Resource.Success(
                    data = localUsers.asExternalModel()
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
                userItemDao.insertAll(users.asEntity())
                val local = userItemDao.searchUser(query = query)
                emit(Resource.Success(data = local.asExternalModel()))
            }
            emit(Resource.Loading(false))
        }
    }

    override suspend fun updateLocalUserItem(userItem: UserItem) {
        userItemDao.update(userItem.asEntity())
    }

    override suspend fun updateLocalUser(user: User) {
        userDao.update(user.asEntity())
    }

    override suspend fun updateUserProfile(
        user: User,
    ): Result<User> {
        return updateUserProfile(
            picture = user.picture,
            banner = user.banner,
            bio = user.bio,
            name = user.name,
            website = user.website,
        )
    }

    override suspend fun updateUserProfile(
        picture: String?,
        banner: String?,
        bio: String?,
        name: String?,
        website: String?,
        favouriteDrinkId: String?,
    ): Result<User> {
        return try {
            val request = UpdateUserRequest.newBuilder()
                .setPicture(picture)
                .setBanner(banner)
                .setBio(bio)
                .setName(name)
                .setWebsite(website)
                .setFavouriteDrinkId(favouriteDrinkId.orEmpty())
                .build()

            val response = userService.updateUser(request)
            val user = response.toUser()
            userDao.insert(user.asEntity())
            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getUsersWithListOfIds(ids: List<String>): List<User> =
        withContext(Dispatchers.IO) {
            return@withContext userDao.getUsersWithListOfIds(ids = ids).map { it.asExternalModel() }
        }

    override fun getUsersIn(userIDs: List<String>): Flow<List<UserItem>> {
        return userItemDao.listUsersIn(userIDs).map { it.asExternalModel() }
    }

    override suspend fun getCurrentUser(): User {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return userDao.getUser(userIdOrUsername = uid).asExternalModel()
    }

    override suspend fun getUserSignIn(userId: String): Flow<Resource<User>> {
        return flow {
            emit(Resource.Loading(true))
            val user = userDao.getUserFlow(userIdOrUsername = userId).firstOrNull()

            if (user != null) {
                emit(Resource.Success(user.asExternalModel()))
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
                userDao.insert(remoteUser.toUser().asEntity())
                emit(Resource.Success(userDao.getUser(userId).asExternalModel()))
            }
            emit(Resource.Loading(false))
        }
    }

    override suspend fun listFriends(userID: UserID): Flow<Resource<List<UserItem>>> =
        flow {
            emit(Resource.Loading(true))

            val request = ListFriendRequest.newBuilder()
                .setUserId(userID)
                .build()

            val remoteFriendRequests = try {
                val response = service.listFriend(request = request)
                val users = response.itemsList.map {
                    it.toUserItem()
                }
                users
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.localizedMessage ?: "Couldn't refresh friend requests"))
                null
            }

            remoteFriendRequests?.let {
                emit(Resource.Success(it))
            }

            emit(Resource.Loading(false))
        }

    override fun getCurrentUserFlow(): Flow<User> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyFlow()
        return userDao.getUserFlow(userIdOrUsername = uid)
            .mapNotNull { it?.asExternalModel() }
    }

    override fun listUser(userIdOrUsername: String): Flow<User> {
        return userDao.getUserFlow(userIdOrUsername = userIdOrUsername)
            .mapNotNull { it?.asExternalModel() }
    }

    override fun getUserItem(userIdOrUsername: String): Flow<UserItem> {
        return userItemDao.getUserItem(userIdOrUsername = userIdOrUsername)
            .mapNotNull { it?.asExternalModel() }
    }

    override fun getUserFlow(userIdOrUsername: String): Flow<User> {
        return userDao.getUserFlow(userIdOrUsername = userIdOrUsername)
            .mapNotNull { it?.asExternalModel() }
    }

    override suspend fun fetchCurrentUser(): Resource<Unit> = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return@withContext Resource.Error("failed to retrieve uid")
        return@withContext fetchUser(uid)
    }

    override suspend fun fetchUser(
        userIDorUsername: String,
    ): Resource<Unit> {
        val request = GetUserRequest.newBuilder()
            .setUserId(userIDorUsername)
            .build()

        return try {
            val remoteUser = userService.getUser(request)
            userDao.insert(remoteUser.toUser().asEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Oups, something went wrong. Please try again later.")
        }
    }

    override suspend fun getSuggestions(): Flow<List<UserSuggestion>> {
        return withContext(Dispatchers.IO) {
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

            return@withContext userDao.getUserSuggestions().map { it.asExternalModel() }
        }
    }

    override suspend fun addTokenToNeo4j(newRegistrationToken: String?) {
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

    override fun uploadProfilePicture(
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

    override fun uploadProfileBanner(
        picture: Uri,
    ) {
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadProfileBanner>().apply {
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

    override suspend fun saveUserPicture(
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