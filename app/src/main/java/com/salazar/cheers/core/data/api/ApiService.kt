package com.salazar.cheers.core.data.api

import com.salazar.cheers.core.data.response.RechargeCoinRequest
import com.salazar.cheers.core.data.response.RechargeCoinResponse
import retrofit2.http.*


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/recharge-coins")
    suspend fun rechargeCoins(
        @Body request: RechargeCoinRequest,
    ): RechargeCoinResponse
}