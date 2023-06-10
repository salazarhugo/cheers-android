package com.salazar.cheers.domain.usecase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.data.user.datastore.StoreUserEmail
import com.salazar.common.di.IODispatcher
import com.salazar.common.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInWithEmailLinkUseCase @Inject constructor(
    private val storeUserEmail: StoreUserEmail,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        emailLink: String,
    ): Resource<Unit> = withContext(ioDispatcher) {
        val auth = Firebase.auth

        if (!auth.isSignInWithEmailLink(emailLink))
            return@withContext Resource.Error("Invalid email link")


        val email = storeUserEmail.getEmail.firstOrNull()
            ?: return@withContext Resource.Error("No stored email")

        try {
            val result = auth.signInWithEmailLink(email, emailLink).await()
            return@withContext Resource.Success(null)
        } catch (e: java.lang.Exception) {
            return@withContext Resource.Error("Error signing in with email link")
        }
    }
}
