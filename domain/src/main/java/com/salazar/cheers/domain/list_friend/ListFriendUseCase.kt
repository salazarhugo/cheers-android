package com.salazar.cheers.domain.list_friend

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.data.user.UserRepository
import com.salazar.common.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListFriendUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
) {
    suspend operator fun invoke(): Flow<Resource<List<UserItem>>> {
        return userRepository.listFriend()
    }
}