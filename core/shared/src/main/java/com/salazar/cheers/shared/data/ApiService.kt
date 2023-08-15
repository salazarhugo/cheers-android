package com.salazar.cheers.shared.data

import com.salazar.cheers.shared.data.request.LoginRequest
import com.salazar.cheers.shared.data.response.LoginResponse
import retrofit2.http.*


interface BffApiService {
    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): LoginResponse

    companion object {
        const val GATEWAY_BASE_URL = "https://android-gateway-clzdlli7.nw.gateway.dev"
    }
}
