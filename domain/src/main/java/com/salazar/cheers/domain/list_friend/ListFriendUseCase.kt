package com.salazar.cheers.domain.list_friend

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.common.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListFriendUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val friendshipRepository: FriendshipRepository,
) {
    suspend operator fun invoke(): Flow<Resource<List<UserItem>>> {
        return userRepositoryImpl.listFriend()
    }
}