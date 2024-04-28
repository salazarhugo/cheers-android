package com.salazar.cheers.data.account

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val localAccountDataSource: LocalAccountDataSource,
) {
    suspend fun putIdToken(idToken: String) {
        localAccountDataSource.putIdToken(idToken = idToken)
    }

    suspend fun putAccount(account: Account) {
        localAccountDataSource.putAccount(account = account)
    }

    suspend fun getAccount(): Account? = localAccountDataSource.getAccount()

    suspend fun getIdToken(): String? = localAccountDataSource.getIdToken()
    suspend fun getAccountFlow(): Flow<Account?> = localAccountDataSource.getAccountFlow()
    suspend fun deleteAccount(): Unit = localAccountDataSource.clear()
}

suspend fun AccountRepository.isConnected(): Boolean = getAccount() != null
suspend fun AccountRepository.isNotConnected(): Boolean = getAccount() == null
