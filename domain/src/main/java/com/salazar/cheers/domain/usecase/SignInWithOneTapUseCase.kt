package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.mapper.toAccount
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.data.user.datastore.StoreUserEmail
import com.salazar.common.di.IODispatcher
import com.salazar.common.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithOneTapUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val storeUserEmail: StoreUserEmail,
    private val dataStoreRepository: DataStoreRepository,
) {
    suspend operator fun invoke(
        idToken: String,
    ): Result<Boolean> = withContext(ioDispatcher) {
        return@withContext try {
            val authResult = authRepository.signInWithOneTap(idToken = idToken)
                ?: return@withContext Result.failure(Exception("Error signing in with one tap"))

            val idToken = authResult.user?.getIdToken(false)?.await()
            dataStoreRepository.updateIdToken(idToken?.token.orEmpty())

            return@withContext authRepository.signIn(idToken = idToken?.token!!)
                .fold(
                    onSuccess = {
                        accountRepository.putAccount(it.toAccount())
                        storeUserEmail.saveEmail(authResult.user?.email ?: "")
                        return@fold Result.success(false)
                    },
                    onFailure = {
                        return@fold Result.failure(it)
                    }
                )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
