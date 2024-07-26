package com.salazar.cheers.domain.register

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.data.user.datastore.StoreUserEmail
import com.salazar.cheers.shared.di.IODispatcher
import com.salazar.cheers.shared.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val storeUserEmail: StoreUserEmail,
    private val userRepositoryImpl: UserRepositoryImpl,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        username: String,
    ): Resource<Unit> = withContext(ioDispatcher) {
        val auth = Firebase.auth

        if (auth.currentUser == null)
            return@withContext Resource.Error("Not signed in")

        val email = storeUserEmail.getEmail.firstOrNull()
            ?: return@withContext Resource.Error("No stored email")

        val result = userRepositoryImpl.createUser(
            username = username,
            email = email,
        )

        if (result.isFailure) {
            return@withContext Resource.Error("Couldn't create user")
        }

        return@withContext Resource.Success(Unit)
    }
}
