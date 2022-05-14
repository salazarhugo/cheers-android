package com.salazar.cheers.backend

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface GoApi {

    @GET("locations")
    suspend fun getLocations(): JsonObject

    @POST("updateLocation")
    suspend fun updateLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    )

    companion object {
        const val BASE_URL = "https://rest-api-r3a2dr4u4a-nw.a.run.app"
    }
}