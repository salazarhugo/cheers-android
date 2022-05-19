package com.salazar.cheers.backend

import com.google.gson.JsonObject
import com.mapbox.geojson.FeatureCollection
import com.salazar.cheers.internal.Event
import com.squareup.moshi.Json
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface GoApi {

    @GET("events")
    suspend fun getEvents(
        @Query("skip") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Event>

    @GET("event/feed")
    suspend fun getEventFeed(
        @Query("skip") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Event>

    @POST("event/create")
    suspend fun createEvent(
        @Body() event: Event,
    )

    @POST("event/update")
    suspend fun updateEvent(
        @Body() event: Event,
    )

    @POST("event/delete")
    suspend fun deleteEvent(
        @Query("eventId") eventId: String,
    )

    @POST("event/interest")
    suspend fun interestEvent(
        @Query("eventId") eventId: String,
    )

    @POST("event/uninterest")
    suspend fun uninterestEvent(
        @Query("eventId") eventId: String,
    )

    @GET("locations")
    suspend fun getLocations(): ResponseBody

    @POST("updateLocation")
    suspend fun updateLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    )

    companion object {
        const val BASE_URL = "https://rest-api-r3a2dr4u4a-nw.a.run.app"
    }
}