package com.salazar.cheers.data.ads

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    private var rewardedAd: RewardedAd? = null

    @SuppressLint("MissingPermission")
    fun init(context: Context) {
        MobileAds.initialize(context)
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("A0357BE5BB04988B430A70B88BEFD68B")).build()
        MobileAds.setRequestConfiguration(configuration)
        loadRewardedAd()
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context, "ca-app-pub-7182026441345500/8592659126", adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    println("ads $adError")
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            }
        )
    }

    fun showRewardedAd(activity: android.app.Activity) {
        rewardedAd?.let { ad ->
            ad.show(
                activity
            ) { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
//                Log.d(TAG, "User earned the reward.")
            }
        } ?: run {
//            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

}