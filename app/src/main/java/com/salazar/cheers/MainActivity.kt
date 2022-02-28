package com.salazar.cheers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.salazar.cheers.ui.chat.ChatViewModel
import com.salazar.cheers.ui.comment.CommentsViewModel
import com.salazar.cheers.ui.detail.PostDetailViewModel
import com.salazar.cheers.ui.event.detail.EventDetailViewModel
import com.salazar.cheers.ui.otherprofile.OtherProfileViewModel
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun postDetailViewModelFactory(): PostDetailViewModel.PostDetailViewModelFactory
        fun eventDetailViewModelFactory(): EventDetailViewModel.EventDetailViewModelFactory
        fun otherProfileViewModelFactory(): OtherProfileViewModel.OtherProfileViewModelFactory
        fun chatViewModelFactory(): ChatViewModel.ChatViewModelFactory
        fun commentsViewModelFactory(): CommentsViewModel.CommentsViewModelFactory
    }

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var paymentSheet: PaymentSheet
    private lateinit var flowController: PaymentSheet.FlowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            if (mainViewModel.completed.value) {
                AlertDialog(
                    onDismissRequest = {
                        mainViewModel.completed.value = false
                    },
                    confirmButton = {},
                    title = { Text("Payment succeeded") },
                    text = {Text("It may take a few minutes before coins are credited to your account")}
                )
            }
            CheersApp(
                presentPaymentSheet = ::presentPaymentSheet,
            )
        }

        PaymentConfiguration.init(
            this,
            "pk_test_51KWqPTAga4Q2CELOu5oK8GHRPlQwVPvcISBMuoWU5yxP8VrtmBhRGm0TBKaKeKm1tz2EY7gmmvvYuFWMJEzWvFhC00qOX6gQb1"
        )
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
//
//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        StrictMode.setThreadPolicy(policy)
//        userConsentPolicy()
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
                merchantDisplayName = "My merchant name",
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
                mainViewModel.completed.value = true
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

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            (mMessageReceiver),
            IntentFilter("NewMessage")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent
        ) {
            mainViewModel.onNewMessage()
        }
    }
}