package com.salazar.cheers.backend

import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.User
import okhttp3.ResponseBody
import retrofit2.http.*


interface GoApi {

    @GET("users/search/{query}")
    suspend fun searchUsers(
        @Path("query") query: String,
    ): List<User>

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

    @POST("follow")
    suspend fun followUser(
        @Query("username") username: String,
    )

    @POST("unfollow")
    suspend fun unfollowUser(
        @Query("username") username: String,
    )

    @POST("posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String,
    )

    @POST("posts/{postId}/unlike")
    suspend fun unlikePost(
        @Path("postId") postId: String,
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