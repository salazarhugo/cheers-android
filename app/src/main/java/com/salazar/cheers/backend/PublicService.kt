package com.salazar.cheers.backend

import retrofit2.http.GET
import retrofit2.http.Path


interface PublicService {

    @GET("users/available/{username}")
    suspend fun isUsernameAvailable(
        @Path("username") username: String,
    ): Boolean

    companion object {
        const val BASE_URL = "https://rest-api-r3a2dr4u4a-nw.a.run.app"
    }
}