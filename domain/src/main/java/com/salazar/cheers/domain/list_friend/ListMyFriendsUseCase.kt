package com.salazar.cheers.domain.list_friend

import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ListMyFriendsUseCase @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val userRepositoryImpl: UserRepositoryImpl,
) {
    suspend operator fun invoke(): Flow<Resource<List<UserItem>>> {
        val account = getAccountUseCase().firstOrNull() ?: return emptyFlow()

        return userRepositoryImpl.listFriends(userID = account.id)
    }
}