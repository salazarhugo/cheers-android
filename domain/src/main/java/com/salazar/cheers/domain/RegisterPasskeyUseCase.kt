package com.salazar.cheers.domain

import android.content.Context
import androidx.credentials.exceptions.CreateCredentialException
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.auth.mapper.toCreatePasskeyRequest
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.shared.data.response.FinishRegistrationResponse
import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterPasskeyUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val authRepository: AuthRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        context: Context,
        username: String,
    ): Result<FinishRegistrationResponse> = withContext(ioDispatcher) {
        return@withContext try {
            // Begin registration
            val result = authRepository.beginRegistration(username = username)
            result.onFailure {
                return@withContext Result.failure(it)
            }
            val beginRegistrationResponse =
                result.getOrNull() ?: return@withContext Result.failure(Exception(""))

            // Launch FIDO flow
            val fidoResult = authRepository.launchFidoFlow(
                activityContext = context,
                request = beginRegistrationResponse.toCreatePasskeyRequest(username = username),
            )

            return@withContext fidoResult.fold(
                onSuccess = { passkey ->
                    // Finish registration
                    val response = authRepository.finishRegistration(
                        username = username,
                        passkey = passkey,
                        challenge = beginRegistrationResponse.publicKey.challenge,
                        userId = beginRegistrationResponse.publicKey.user.id,
                    ).getOrNull()
                        ?: return@withContext Result.failure(Exception("missing id token"))

                    // Save username in data store
                    dataStoreRepository.updateUsername(username)

                    Result.success(response)
                },
                onFailure = {
                    Result.failure(it)
                }
            )
        } catch (e: CreateCredentialException) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
