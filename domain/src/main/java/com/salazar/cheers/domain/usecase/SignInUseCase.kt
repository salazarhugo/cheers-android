package com.salazar.cheers.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.account.mapper.toAccount
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.data.user.datastore.StoreUserEmail
import com.salazar.cheers.domain.get_id_token.GetIdTokenUseCase
import com.salazar.common.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val storeUserEmail: StoreUserEmail,
    private val getIdTokenUseCase: GetIdTokenUseCase,
) {
    suspend operator fun invoke(): Result<Boolean> = withContext(ioDispatcher) {
        return@withContext try {
            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: return@withContext Result.failure(Exception("not signed in with firebase"))

            val idToken = getIdTokenUseCase()

            return@withContext when (idToken) {
                is com.salazar.common.util.result.Result.Success -> {
                    authRepository.signIn(idToken = idToken.data)
                        .fold(
                            onSuccess = {
                                accountRepository.putAccount(it.toAccount())
                                storeUserEmail.saveEmail(currentUser.email.orEmpty())
                                return@fold Result.success(false)
                            },
                            onFailure = {
                                return@fold Result.failure(it)
                            }
                        )
                }
                is com.salazar.common.util.result.Result.Error -> {
                    Result.failure(Throwable(""))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
