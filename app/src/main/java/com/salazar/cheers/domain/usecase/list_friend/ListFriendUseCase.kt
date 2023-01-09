package com.salazar.cheers.domain.usecase.list_friend

import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class ListFriendUseCase @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
){
    suspend operator fun invoke(): Flow<List<UserItem>> {
        return friendshipRepository.listFriend()
            .filter { it is Resource.Success }
            .mapNotNull { it.data }
    }
}