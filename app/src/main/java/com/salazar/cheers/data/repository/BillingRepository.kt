package com.salazar.cheers.data.repository

import android.app.Activity
import android.app.Application
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.data.api.ApiService
import com.salazar.cheers.core.data.response.RechargeCoinRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    val application: Application,
    private val apiService: ApiService,
) : PurchasesUpdatedListener, PurchasesResponseListener {

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // TODO Handle an error caused by a user cancelling the purchase flow.
        } else {
            // TODO Handle any other error codes.
        }
    }

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    var billingClient = BillingClient.newBuilder(application)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private suspend fun verifyPurchase(purchase: Purchase): Boolean {
        val request = RechargeCoinRequest(
            packageName = purchase.packageName,
            productId = purchase.products[0],
            purchaseToken = purchase.purchaseToken,
        )

        return try {
            val response = apiService.rechargeCoins(request = request)
            true
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED || purchase.isAcknowledged) {
            return
        }

        GlobalScope.launch {
            verifyPurchase(purchase = purchase)
            consumePurchase(purchaseToken = purchase.purchaseToken)
        }
    }

    private suspend fun consumePurchase(purchaseToken: String): ConsumeResult {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()

        val consumeResult = withContext(Dispatchers.IO) {
            billingClient.consumePurchase(consumeParams)
        }

        return consumeResult
    }

    fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d("Billing", billingResult.toString())
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                }
            }

            override fun onBillingServiceDisconnected() {
            }
        })
    }

    fun launchBillingFlow(
        activity: Activity,
        productDetails: ProductDetails
    ) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .setObfuscatedAccountId(FirebaseAuth.getInstance().currentUser?.uid!!)
            .build()

        val responseCode = billingClient.launchBillingFlow(
            activity,
            flowParams
        ).responseCode
    }

    suspend fun queryProductDetails(): ProductDetailsResult {
        val productList = ArrayList<String>()
        productList.add("coins_36")
        productList.add("coins_70")
        productList.add("coins_350")
        productList.add("coins_700")
        productList.add("coins_1400")
        productList.add("coins_3500")
        productList.add("coins_7000")
        productList.add("coins_17500")

        val products = productList.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        // leverage queryProductDetails Kotlin extension function
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }

        return productDetailsResult
    }

}