package com.salazar.cheers.domain

import android.content.Context
import com.salazar.cheers.domain.usecase.SignInWithPasskeyUseCase
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterPasskeyAndSignInUseCase @Inject constructor(
    private val registerPasskeyUseCase: RegisterPasskeyUseCase,
    private val signInWithPasskeyUseCase: SignInWithPasskeyUseCase,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        context: Context,
        username: String,
    ): Result<Unit> = withContext(ioDispatcher) {
        return@withContext registerPasskeyUseCase(context, username).fold(
            onSuccess = { response ->
                return@fold signInWithPasskeyUseCase(
                    user = response.user,
                    customToken = response.token,
                ).fold(
                    onSuccess = { Result.success(Unit) },
                    onFailure = { Result.failure(it) },
                )
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}
