package com.salazar.cheers.domain.usecase

import com.salazar.cheers.core.db.CheersDatabase
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.domain.update_id_token.UpdateIdTokenUseCase
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val database: CheersDatabase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() {
        withContext(ioDispatcher) {
            authRepository.signOut()
            accountRepository.deleteAccount()
            database.clearAllTables()
        }
    }
}
