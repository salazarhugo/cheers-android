package com.salazar.cheers.domain.usecase

import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.mapper.toAccount
import com.salazar.cheers.data.auth.AuthRepository
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
) {
    suspend operator fun invoke(
        idToken: String?,
    ): Resource<Boolean> = withContext(ioDispatcher) {
        return@withContext try {
            val authResult = authRepository.signInWithOneTap(idToken = idToken)
                ?: return@withContext Resource.Error("Error signing in with one tap")

            val idToken = authResult.user?.getIdToken(false)?.await()

            return@withContext authRepository.signIn(idToken = idToken?.token!!)
                .fold(
                    onSuccess = {
                        accountRepository.putAccount(it.toAccount())
                        storeUserEmail.saveEmail(authResult.user?.email ?: "")
                        return@fold Resource.Success(false)
                    },
                    onFailure = {
                        return@fold when(it) {
                            is AuthRepository.NotRegisteredException -> Resource.Success(true)
                            else -> Resource.Error(it.localizedMessage)
                        }
                    }
                )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Resource.Error("Error signing in with one tap")
        }
    }
}
