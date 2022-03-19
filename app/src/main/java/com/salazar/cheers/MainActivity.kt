package com.salazar.cheers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.repository.BillingRepository
import com.salazar.cheers.ui.main.chat.ChatViewModel
import com.salazar.cheers.ui.main.comment.CommentsViewModel
import com.salazar.cheers.ui.main.detail.PostDetailViewModel
import com.salazar.cheers.ui.main.event.detail.EventDetailViewModel
import com.salazar.cheers.ui.main.otherprofile.OtherProfileViewModel
import com.salazar.cheers.ui.sheets.SendGiftViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun postDetailViewModelFactory(): PostDetailViewModel.PostDetailViewModelFactory
        fun eventDetailViewModelFactory(): EventDetailViewModel.EventDetailViewModelFactory
        fun otherProfileViewModelFactory(): OtherProfileViewModel.OtherProfileViewModelFactory
        fun chatViewModelFactory(): ChatViewModel.ChatViewModelFactory
        fun commentsViewModelFactory(): CommentsViewModel.CommentsViewModelFactory
        fun sendGiftViewModelFactory(): SendGiftViewModel.SendGiftViewModelFactory
    }

    private val cheersViewModel: CheersViewModel by viewModels()
    lateinit var paymentSheet: PaymentSheet
    private var mInterstitialAd: InterstitialAd? = null

    @Inject
    lateinit var billingRepository: BillingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            if (cheersViewModel.completed.value) {
//                AlertDialog(
//                    onDismissRequest = {
//                        cheersViewModel.completed.value = false
//                    },
//                    confirmButton = {},
//                    title = { Text("Payment succeeded") },
//                    text = { Text("It may take a few minutes before coins are credited to your account") }
//                )
            }
            CheersApp(
                presentPaymentSheet = ::presentPaymentSheet,
                showInterstitialAd = ::showInterstitialAd,
            )
        }

        PaymentConfiguration.init(
            this,
            "pk_live_51KWqPTAga4Q2CELO2K93NrScmrQOQf0Pbvn0XpDXSqW4gzgXFWpMx1lnSjTfR8251B3TI4zHmQ0MqFDCYdpKD2D200KhtML5F7"
        )
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
//
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        userConsentPolicy()
        initInterstitialAd()
    }

    private fun presentPaymentSheet(clientSecret: String) {
        val googlePayConfiguration = PaymentSheet.GooglePayConfiguration(
            environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
            countryCode = "US",
            currencyCode = "USD" // Required for Setup Intents, optional for Payment Intents
        )
        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Cheers",
                allowsDelayedPaymentMethods = true,
                googlePay = googlePayConfiguration
            )
        )
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                cheersViewModel.completed.value = true
                print("Completed")
            }
        }
    }

    private fun userConsentPolicy() {
        // Set tag for underage of consent. false means users are not underage.
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, params, {
            if (consentInformation.isConsentFormAvailable)
                loadForm(consentInformation = consentInformation)
        },
            {
            })
    }

    fun showInterstitialAd() {
        mInterstitialAd?.show(this)
    }

    fun initInterstitialAd() {
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("2C6292E9B3EBC9CF72C85D55627B6D2D")).build()
        MobileAds.setRequestConfiguration(configuration)

        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-7182026441345500/2922347081",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(@NonNull interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

    private fun loadForm(consentInformation: ConsentInformation) {
        UserMessagingPlatform.loadConsentForm(
            this,
            { consentForm ->
                val consentForm = consentForm
                if (consentInformation.getConsentStatus() === ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(
                        this@MainActivity
                    ) { // Handle dismissal by reloading form.
                        loadForm(consentInformation)
                    }
                }
            }
        ) {
        }
    }

    override fun onResume() {
        super.onResume()

        cheersViewModel.queryPurchases()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            (mMessageReceiver),
            IntentFilter("NewMessage")
        )
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent
        ) {
            cheersViewModel.onNewMessage()
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        Log.i("AUTH", p0.currentUser?.uid.toString())
        cheersViewModel.onAuthChange(p0)
    }

}