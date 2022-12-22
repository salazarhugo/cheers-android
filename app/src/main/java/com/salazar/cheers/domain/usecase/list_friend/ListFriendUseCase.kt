package com.salazar.cheers.domain.usecase.list_friend

import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListFriendUseCase @Inject constructor(
    private val repository: UserRepository,
){
    operator fun invoke(): Flow<List<UserItem>> {
        return repository.listFriend()
    }
}