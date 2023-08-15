package com.salazar.cheers.data.billing.api

import com.salazar.cheers.data.billing.response.RechargeCoinRequest
import com.salazar.cheers.data.billing.response.RechargeCoinResponse
import retrofit2.http.*


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/recharge-coins")
    suspend fun rechargeCoins(
        @Body request: RechargeCoinRequest,
    ): RechargeCoinResponse
}