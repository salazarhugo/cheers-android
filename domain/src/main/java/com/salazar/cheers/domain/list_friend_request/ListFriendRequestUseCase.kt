package com.salazar.cheers.domain.list_friend_request

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ListFriendRequestUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val friendshipRepository: FriendshipRepository,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): Flow<List<UserItem>> = withContext(ioDispatcher) {
        val uid = getAccountUseCase().first()?.id ?: return@withContext flowOf(emptyList())
        return@withContext friendshipRepository.fetchFriendRequest(uid)
            .fold(
                onSuccess = ::flowOf,
                onFailure = {
                    flowOf(emptyList())
                }
            )
    }
}
