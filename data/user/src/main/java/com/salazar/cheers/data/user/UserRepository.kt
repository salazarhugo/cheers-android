package com.salazar.cheers.data.user

import android.net.Uri
import com.salazar.cheers.core.model.CheckUsernameResult
import com.salazar.cheers.core.model.Gender
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.UserStats
import com.salazar.cheers.core.model.UserSuggestion
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun listSuggestions(): Result<List<UserItem>>

    suspend fun checkUsername(
        username: String,
    ): Result<CheckUsernameResult>

    suspend fun createUser(
        username: String,
        email: String,
    ): Result<User>

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
        updateMask: List<String>? = null,
    ): Result<User>

    suspend fun updateUserProfile(
        picture: String? = null,
        banner: List<String>? = null,
        bio: String? = null,
        name: String? = null,
        website: String? = null,
        favouriteDrinkId: String? = null,
        gender: Gender? = null,
        jobTitle: String = "",
        jobCompany: String = "",
        education: String = "",
        updateMask: List<String>? = null,
    ): Result<User>

    suspend fun getUsersWithListOfIds(ids: List<String>): List<User>

    fun getUsersIn(userIDs: List<String>): Flow<List<UserItem>>

    suspend fun getCurrentUser(): User

    suspend fun getUserSignIn(userId: String): Flow<Resource<User>>

    suspend fun listFriends(userID: String): Flow<Resource<List<UserItem>>>

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
        banners: List<Uri>,
    )

    suspend fun saveUserPicture(
        picture: String,
    )
}