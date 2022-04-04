package com.salazar.cheers.data.repository

import android.app.Activity
import android.app.Application
import android.util.Log
import com.android.billingclient.api.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    val application: Application,
) : PurchasesUpdatedListener, PurchasesResponseListener {

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        Log.d("BILLING", billingResult.toString())
        Log.d("BILLING", purchases.toString())
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged)
                    verifyPurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
        }
    }

    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged)
                    verifyPurchase(purchase)
            }
        }
    }

    var billingClient = BillingClient.newBuilder(application)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private fun handlePurchaseCloud(purchase: Purchase): Task<HashMap<*, *>> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "packageName" to purchase.packageName,
            "productId" to purchase.skus[0],
            "purchaseToken" to purchase.purchaseToken,
        )

        return FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("rechargeCoins")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as HashMap<*, *>
                result
            }
    }

    suspend fun queryPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, this)
    }

    private fun verifyPurchase(purchase: Purchase) {
        handlePurchaseCloud(purchase = purchase).addOnSuccessListener {
            GlobalScope.launch {
                consumePurchase(purchase = purchase)
            }
        }
    }

    private suspend fun consumePurchase(purchase: Purchase) = withContext(Dispatchers.IO) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        billingClient.consumePurchase(consumeParams)
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
        skuDetails: SkuDetails
    ) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .setObfuscatedAccountId(FirebaseAuth.getInstance().currentUser?.uid!!)
            .build()
        val responseCode = billingClient.launchBillingFlow(
            activity,
            flowParams
        ).responseCode
    }

    suspend fun querySkuDetails(): SkuDetailsResult {
        val skuList = ArrayList<String>()
        skuList.add("coins_36")
        skuList.add("coins_70")
        skuList.add("coins_350")
        skuList.add("coins_700")
        skuList.add("coins_1400")
        skuList.add("coins_3500")
        skuList.add("coins_7000")
        skuList.add("coins_17500")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        return withContext(Dispatchers.IO) {
            return@withContext billingClient.querySkuDetails(params.build())
        }
    }

}