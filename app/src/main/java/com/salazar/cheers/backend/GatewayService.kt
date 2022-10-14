package com.salazar.cheers.backend

import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.data.entities.UserSuggestion
import com.salazar.cheers.internal.*
import okhttp3.ResponseBody
import retrofit2.http.*


interface GatewayService {

    @GET("posts/feed")
    suspend fun postFeed(
        @Query("page") skip: Int,
        @Query("pageSize") pageSize: Int,
    ): List<Post>

    @POST("parties")
    suspend fun createParty(
        @Body() party: CreatePartyRequest,
    )

    companion object {
        const val GATEWAY_URL = "https://android-gateway-clzdlli7.nw.gateway.dev"
    }
}