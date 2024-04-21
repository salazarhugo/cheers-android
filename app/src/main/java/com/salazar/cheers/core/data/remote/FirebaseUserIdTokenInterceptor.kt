package com.salazar.cheers.core.data.remote

import com.salazar.cheers.domain.get_id_token.GetIdTokenUseCase
import com.salazar.common.util.result.Result
import com.salazar.common.util.result.getOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.String.format
import javax.inject.Inject


class FirebaseUserIdTokenInterceptor @Inject constructor(
    private val getIdTokenUseCase: GetIdTokenUseCase,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        return try {
            val idToken = runBlocking(Dispatchers.IO) {
                getIdTokenUseCase().getOrThrow()
            }
            val modifiedRequest: Request = request.newBuilder()
                .addHeader(X_FIREBASE_ID_TOKEN, format("Bearer %s", idToken))
                .build()
            chain.proceed(modifiedRequest)
        } catch (e: Exception) {
            throw IOException(e.message)
        }
    }

    companion object {
        private const val X_FIREBASE_ID_TOKEN = "Authorization"
    }
}
