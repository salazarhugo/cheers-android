package com.salazar.cheers.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.ads.initializeAds
import com.salazar.cheers.core.ui.CheersViewModel
import com.salazar.common.util.LocalActivity
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private val cheersViewModel: CheersViewModel by viewModels()

    @Inject
    lateinit var dataStoreRepository: com.salazar.cheers.data.user.datastore.DataStoreRepository

    @Inject
    lateinit var friendshipRepository: com.salazar.cheers.data.friendship.FriendshipRepository

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    private val updateType = AppUpdateType.IMMEDIATE

    private lateinit var consentInformation: ConsentInformation
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        checkForAppUpdates()

        setContent {
            CompositionLocalProvider(LocalActivity provides this) {
                CheersApp()
            }
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        userConsentPolicy()
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("INTENT", intent.data.toString())
        val data = intent.data ?: return
        val auth = Firebase.auth

        if (!auth.isSignInWithEmailLink(data.toString()))
            return

        val continueUrl = data.getQueryParameter("continueUrl") ?: return
        val encodedUrl = URLEncoder.encode(data.toString(), StandardCharsets.UTF_8.toString())

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "$continueUrl/$encodedUrl".toUri(),
            this,
            MainActivity::class.java,
        )

        startActivity(deepLinkIntent)
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(updateType)

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    this,
                    AppUpdateOptions.defaultOptions(updateType),
                    1,
                )
            }
        }
    }

    private fun userConsentPolicy() {
        val debugSettings = ConsentDebugSettings.Builder(this)
            .addTestDeviceHashedId("8DB645BA1C4E36F115428DBFA0C0CAFE")
            .build()

        // Set tag for underage of consent. false means users are not underage.
        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this@MainActivity) { loadAndShowError ->
                    // Consent has been gathered.
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk()
                    }
                }
            },
            { requestConsentError ->
                // Consent gathering failed.
                Log.w(
                    "MainActivity",
                    String.format("%s: %s", requestConsentError.errorCode, requestConsentError.message),
                )
            },
        )
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

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        initializeAds(this)
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        Log.i("AUTH", p0.currentUser?.uid.toString())
        cheersViewModel.onAuthChange(p0)
    }
}