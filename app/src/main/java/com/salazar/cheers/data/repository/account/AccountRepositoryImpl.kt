package com.salazar.cheers.data.repository.account

import cheers.account.v1.Account
import cheers.account.v1.AccountServiceGrpcKt
import cheers.account.v1.GetAccountRequest
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountService: AccountServiceGrpcKt.AccountServiceCoroutineStub,
): AccountRepository {

    override suspend fun getAccount(): Result<Account> {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid!!
            val request = GetAccountRequest.newBuilder()
                .setAccountId(uid)
                .build()
            val response = accountService.getAccount(request = request)
            Result.success(response.account)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}