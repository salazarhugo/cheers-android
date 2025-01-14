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
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    application: Application,
) : PurchasesUpdatedListener, PurchasesResponseListener {

    private val _purchaseUpdateLiveData = Channel<List<Purchase>?>(Channel.BUFFERED)
    val purchaseUpdateFlowData: Flow<List<Purchase>?> = _purchaseUpdateLiveData.receiveAsFlow()

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.d("Billing", "On purchase updated $purchases")
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            runBlocking {
                _purchaseUpdateLiveData.send(purchases)
            }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            // TODO Handle an error caused by a user cancelling the purchase flow.
        } else {
            // TODO Handle any other error codes.
        }
    }

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>
    ) {
    }

    private var billingClient = BillingClient.newBuilder(application)
        .setListener(this)
        .enableUserChoiceBilling { }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enablePrepaidPlans()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    fun startConnection() {
        if (billingClient.connectionState == BillingClient.ConnectionState.CONNECTED) return

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
        productDetailsParam: com.salazar.cheers.core.model.ProductDetails,
        offerToken: String,
    ): Int {
        val productDetails = getProduct(
            productId = productDetailsParam.id,
            productType = productDetailsParam.type,
        ).getOrNull() ?: return -1

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .also { if (offerToken.isNotEmpty()) it.setOfferToken(offerToken) }
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .setObfuscatedAccountId(userID)
            .setObfuscatedProfileId(userID)
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
        productType: String = BillingClient.ProductType.SUBS,
    ): Result<ProductDetails> {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(productType)
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

    fun listSubscriptions() {
    }
}