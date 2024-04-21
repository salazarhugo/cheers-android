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
import com.salazar.cheers.core.db.dao.UserDao
import com.salazar.cheers.core.db.dao.UserItemDao
import com.salazar.cheers.core.db.dao.UserStatsDao
import com.salazar.cheers.core.db.model.asEntity
import com.salazar.cheers.core.db.model.asExternalModel
import com.salazar.cheers.core.model.RecentUser
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.UserStats
import com.salazar.cheers.core.model.UserSuggestion
import com.salazar.cheers.data.user.workers.UploadProfileBanner
import com.salazar.cheers.data.user.workers.UploadProfilePicture
import com.salazar.cheers.shared.data.mapper.toUserItem
import com.salazar.common.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun listSuggestions(): Result<List<UserItem>>

    suspend fun checkUsername(
        username: String,
    ): Result<Boolean>

    suspend fun createUser(
        username: String,
        email: String,
    ): Result<User>

    suspend fun insertRecent(username: String)
    suspend fun blockUser(userId: String)

    suspend fun getFollowers(userIdOrUsername: String): List<UserItem>?
    fun listUserItems(): Flow<List<UserItem>>

    suspend fun getFollowing(userIdOrUsername: String): List<UserItem>?

    suspend fun getUserStats(username: String): UserStats


    suspend fun queryUsers(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<UserItem>>>

    suspend fun updateLocalUserItem(userItem: UserItem)

    suspend fun updateLocalUser(user: User)

    suspend fun updateUserProfile(
        user: User,
    ): Result<User>

    suspend fun updateUserProfile(
        picture: String? = null,
        banner: String? = null,
        bio: String? = null,
        name: String? = null,
        website: String? = null,
    ): Result<User>

    suspend fun getUsersWithListOfIds(ids: List<String>): List<User>

    fun getUsersIn(userIDs: List<String>): Flow<List<UserItem>>

    suspend fun getCurrentUser(): User

    suspend fun getUserSignIn(userId: String): Flow<Resource<User>>

    suspend fun listFriend(): Flow<Resource<List<UserItem>>>

    fun getCurrentUserFlow(): Flow<User>

    fun listUser(userIdOrUsername: String): Flow<User>

    fun getUserItem(userIdOrUsername: String): Flow<UserItem>

    fun getUserFlow(userIdOrUsername: String): Flow<User>

    suspend fun fetchCurrentUser(): Resource<Unit>

    suspend fun fetchUser(
        userIDorUsername: String,
    ): Resource<Unit>

    suspend fun getSuggestions(): Flow<List<UserSuggestion>>

    suspend fun addTokenToNeo4j(newRegistrationToken: String?)

    fun uploadProfilePicture(
        picture: Uri,
    )

    fun uploadProfileBanner(
        picture: Uri,
    )

    suspend fun saveUserPicture(
        picture: String,
    )
}