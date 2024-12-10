package com.salazar.cheers.shared.data

import com.salazar.cheers.shared.data.request.FinishLoginRequest
import com.salazar.cheers.shared.data.request.FinishRegistrationRequest
import com.salazar.cheers.shared.data.request.LoginRequest
import com.salazar.cheers.shared.data.response.BeginLoginResponse
import com.salazar.cheers.shared.data.response.BeginRegistrationResponse
import com.salazar.cheers.shared.data.response.FinishLoginResponse
import com.salazar.cheers.shared.data.response.FinishRegistrationResponse
import com.salazar.cheers.shared.data.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface BffApiService {
    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): LoginResponse

    @GET("v1/auth/login/begin/{username}")
    suspend fun beginLogin(
        @Path("username") username: String,
    ): BeginLoginResponse

    @POST("v1/auth/login/finish")
    suspend fun finishLogin(
        @Body request: FinishLoginRequest,
    ): FinishLoginResponse

    @GET("v1/auth/register/begin/{username}")
    suspend fun beginRegistration(
        @Path("username") username: String,
    ): BeginRegistrationResponse

    @POST("v1/auth/register/finish")
    suspend fun finishRegistration(
        @Body request: FinishRegistrationRequest,
    ): FinishRegistrationResponse

    companion object {
        const val GATEWAY_BASE_URL = "https://api.cheers.social"
    }
}
