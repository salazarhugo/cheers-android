package com.salazar.cheers.domain.usecase

import android.content.Context
import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.salazar.cheers.data.account.AccountRepository
import com.salazar.cheers.data.auth.AuthRepository
import com.salazar.cheers.data.auth.mapper.toPasskey
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import com.salazar.cheers.shared.data.response.BeginLoginResponse
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithCredentialManagerFlowUseCase @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithPasskeyUseCase: SignInWithPasskeyUseCase,
    private val dataStoreRepository: DataStoreRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        showGoogleOptions: Boolean,
        activityContext: Context,
        username: String?,
    ): Flow<Resource<Throwable>> = withContext(ioDispatcher) {
        return@withContext flow {

            // Get the username from the data store.
            val username1 = username ?: dataStoreRepository.getUsername().firstOrNull()

            // Get the challenge from the server.
            val beginLoginResponse = authRepository.beginLogin(username = username1).getOrNull()

            val result = authRepository.showSigninOptions(
                showGoogleOptions = showGoogleOptions,
                activityContext = activityContext,
                beginLoginResponse = beginLoginResponse,
            )

            result.fold(
                onSuccess = {
                    emit(Resource.Loading(isLoading = true))
                    emit(
                        handleSignIn(
                            username = username1,
                            beginLoginResponse = beginLoginResponse,
                            result = it,
                        )
                            .fold(
                                onSuccess = { Resource.Success(Throwable()) },
                                onFailure = { Resource.Error(it.localizedMessage, it) },
                            )
                    )
                    emit(Resource.Loading(isLoading = false))
                },
                onFailure = {
                    emit(Resource.Error(it.localizedMessage))
                }
            )
        }
    }

    private suspend fun handleSignIn(
        result: GetCredentialResponse,
        username: String?,
        beginLoginResponse: BeginLoginResponse?,
    ): Result<Unit> {
        val credential = result.credential

        return when (credential) {
            // Passkey
            is PublicKeyCredential -> {
                val response = authRepository.parseGetPasskeyResponse(credential)
                    ?: return Result.failure(Exception("failed to parse get passkey response"))
                if (username == null || beginLoginResponse == null) {
                    return Result.failure(Exception(""))
                }
                authRepository.finishLogin(
                    username = username,
                    passkey = response.toPasskey(),
                    challenge = beginLoginResponse.publicKey.challenge,
                ).fold(
                    onSuccess = { response ->
                        return signInWithPasskeyUseCase(
                            user = response.user,
                            customToken = response.token,
                        )
                    },
                    onFailure = {
                        return Result.failure(it)
                    }
                )
            }

            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                signInWithEmailAndPasswordUseCase(
                    email = username,
                    password = password,
                )
            }

            is CustomCredential -> {
                if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Catch any unrecognized custom credential type here.
                    Log.e("Passkeys", "Unexpected type of credential")
                    return Result.failure(Exception("Unexpected type of credential"))
                }

                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    signInWithGoogleUseCase(
                        idToken = googleIdTokenCredential.idToken
                    ).fold(
                        onSuccess = {
                            return Result.success(Unit)
                        },
                        onFailure = {
                            it.printStackTrace()
                            return Result.failure(it)
                        }
                    )
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("Passkeys", "Received an invalid google id token response", e)
                    Result.failure(e)
                }
            }

            else -> Result.failure(Exception("Unexpected type of credential"))
        }
    }
}
