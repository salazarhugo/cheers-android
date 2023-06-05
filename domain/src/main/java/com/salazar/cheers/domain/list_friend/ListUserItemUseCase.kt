package com.salazar.cheers.domain.list_friend

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ListUserItemUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
) {
    suspend operator fun invoke(users: Flow<List<UserItem>>): Flow<List<UserItem>> {
        val friends = friendshipRepository.listFriend()
        val friendRequests = friendshipRepository.listFriend()

        return combine(users, friends, friendRequests) { a, friends, friendRequests ->
            return@combine a.map { user ->
                val isFriend = friends.contains(user.id)
                val hasRequested = friendRequests.contains(user.id)
                user.copy(
                    friend = isFriend,
                    requested = hasRequested
                )
            }
        }
    }
}