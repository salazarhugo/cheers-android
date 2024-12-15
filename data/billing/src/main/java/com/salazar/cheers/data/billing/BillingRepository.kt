package com.salazar.cheers.data.billing

import android.app.Activity
import android.app.Application
import android.content.res.Resources.NotFoundException
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.UserChoiceBillingListener
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.salazar.cheers.data.billing.api.ApiService
import com.salazar.cheers.data.billing.response.RechargeCoinRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    application: Application,
    private val apiService: ApiService,
) : PurchasesUpdatedListener, PurchasesResponseListener {

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.d("Billing", "On purchase updated $purchases")
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
        .enableUserChoiceBilling(UserChoiceBillingListener { })
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enablePrepaidPlans()
                .enableOneTimeProducts()
                .build(),
        )
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
        } catch (e: Exception) {
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
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    Log.d("Billing", "BillingClient is ready. Connection started")
                    // The BillingClient is ready. You can query purchases here.
                } else {
                    Log.e("Billing", billingResult.toString())
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e("Billing", "Billing service disconnected")
            }
        })
    }

    suspend fun launchBillingFlow(
        userID: String,
        activity: Activity,
        productDetails: com.salazar.cheers.core.model.ProductDetails,
    ): Int {
        val productDetails = getProduct(productId = productDetails.id).getOrNull() ?: return -1
        val offerToken =
            productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return -1

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .setObfuscatedAccountId(userID)
            .build()

        val responseCode = billingClient.launchBillingFlow(
            activity,
            flowParams
        ).responseCode

        return responseCode
    }

    suspend fun queryProductDetails(): Result<List<com.salazar.cheers.core.model.ProductDetails>> {
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

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }
        Log.d("HUGO", productDetailsResult.productDetailsList.toString())
        Log.d("HUGO", productDetailsResult.billingResult.debugMessage.toString())
        productDetailsResult.billingResult.responseCode
        val productDetails = productDetailsResult.productDetailsList?.map { it.toProductDetails() }
            ?: return Result.failure(NotFoundException())

        return Result.success(productDetails)
    }

    suspend fun getSubProduct(
        productId: String,
    ): Result<com.salazar.cheers.core.model.ProductDetails> {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }

        val a =
            productDetailsResult.productDetailsList?.map { it.toProductDetails() }?.firstOrNull()
        if (a != null) {
            return Result.success(a)
        }

        return Result.failure(NotFoundException())
    }

    private suspend fun getProduct(
        productId: String,
    ): Result<ProductDetails> {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }

        Log.e("BillingRepository", productDetailsResult.billingResult.toString())
        Log.e("BillingRepository", productDetailsResult.productDetailsList.toString())
        val a = productDetailsResult.productDetailsList?.firstOrNull()
        if (a != null) {
            return Result.success(a)
        }

        Log.e("BillingRepository", "product not found with id $productId")
        return Result.failure(NotFoundException())
    }
}