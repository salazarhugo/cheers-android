package com.salazar.cheers.backend

import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.data.entities.UserSuggestion
import com.salazar.cheers.internal.*
import okhttp3.ResponseBody
import retrofit2.http.*


interface GatewayService {

    @GET("users/suggestions")
    suspend fun suggestions(): List<UserSuggestion>

    @POST("comment")
    suspend fun postComment(
        @Body comment: Comment,
    )

    @GET("users/search/{query}")
    suspend fun searchUsers(
        @Path("query") query: String,
    ): List<User>

    @GET("users/activity")
    suspend fun getActivity(): List<Activity>

    @GET("events")
    suspend fun getEvents(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Party>

    @GET("party/feed")
    suspend fun getPartyFeed(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Party>

    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("posts/{userId}")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Post>

    @GET("posts/feed")
    suspend fun postFeed(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Post>

    @GET("stories/{username}")
    suspend fun getUserStory(
        @Path("username") username: String,
    ): List<Story>

    @GET("stories/feed")
    suspend fun storyFeed(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Story>

    @POST("posts/create")
    suspend fun createPost(
        @Body() post: Post,
    )

    @POST("stories/create")
    suspend fun createStory(
        @Body() story: Story,
    )

    @POST("users/create")
    suspend fun createUser(
        @Body() user: User,
    ): User

    @POST("parties")
    suspend fun createParty(
        @Body() party: CreatePartyRequest,
    )

    @POST("event/update")
    suspend fun updateEvent(
        @Body() party: Party,
    )

    @POST("users/tokens/{token}")
    suspend fun addRegistrationToken(
        @Path("token") token: String,
    )

    @GET("event/{eventId}/interested/list")
    suspend fun interestedList(
        @Path("eventId") eventId: String,
    ): List<User>

    @GET("event/{eventId}/going/list")
    suspend fun goingList(
        @Path("eventId") eventId: String,
    ): List<User>

    @GET("followers/list")
    suspend fun followersList(
        @Query("userIdOrUsername") userIdOrUsername: String,
    ): List<User>

    @GET("following/list")
    suspend fun followingList(
        @Query("userIdOrUsername") userIdOrUsername: String,
    ): List<User>

    @PATCH("users")
    suspend fun updateUser(
        @Body() user: User,
    )

    @GET("users/{userIdOrUsername}")
    suspend fun getUser(
        @Path("userIdOrUsername") userIdOrUsername: String,
    ): User

    @POST("users/{userId}/block")
    suspend fun blockUser(
        @Path("userId") userId: String,
    )

    @POST("stories/{storyId}/seen")
    suspend fun seenStory(
        @Path("storyId") storyId: String,
    )

    @DELETE("posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: String,
    )

    @POST("stories/{storyId}")
    suspend fun deleteStory(
        @Path("storyId") storyId: String,
    )

    @POST("events/{eventId}")
    suspend fun deleteEvent(
        @Path("eventId") eventId: String,
    )

    @POST("follow")
    suspend fun followUser(
        @Query("username") username: String,
    )

    @POST("unfollow")
    suspend fun unfollowUser(
        @Query("username") username: String,
    )

    @GET("posts/{postId}/members")
    suspend fun postMembers(
        @Path("postId") postId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): List<User>

    @POST("posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String,
    )

    @POST("posts/{postId}/unlike")
    suspend fun unlikePost(
        @Path("postId") postId: String,
    )

    @POST("event/{eventId}/going")
    suspend fun goingEvent(
        @Path("eventId") eventId: String,
    )

    @POST("event/{eventId}/ungoing")
    suspend fun ungoingEvent(
        @Path("eventId") eventId: String,
    )

    @POST("party/interest")
    suspend fun interestEvent(
        @Query("partyId") partyId: String,
    )

    @POST("party/uninterest")
    suspend fun uninterestEvent(
        @Query("partyId") partyId: String,
    )

    @GET("locations")
    suspend fun getLocations(): ResponseBody

    @POST("updateLocation")
    suspend fun updateLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    )

    companion object {
        const val GATEWAY_URL = "https://android-gateway-clzdlli7.nw.gateway.dev"
    }
}