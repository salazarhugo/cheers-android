package com.salazar.cheers.friendship.domain.usecase.list_friend

import com.salazar.cheers.core.data.Resource
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class ListFriendUseCase @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
){
    suspend operator fun invoke(userID: String): Flow<List<UserItem>> {
        return friendshipRepository.listFriend(userId = userID)
            .filter { it is Resource.Success }
            .mapNotNull { it.data }
    }
}