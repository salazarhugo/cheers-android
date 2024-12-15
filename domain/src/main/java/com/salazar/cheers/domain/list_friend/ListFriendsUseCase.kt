package com.salazar.cheers.domain.list_friend

import com.salazar.cheers.core.model.UserID
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListFriendsUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
) {
    suspend operator fun invoke(
        otherUserID: UserID,
    ): Flow<Resource<List<UserItem>>> {
        return userRepositoryImpl.listFriends(userID = otherUserID)
    }
}