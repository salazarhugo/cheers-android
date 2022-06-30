package com.salazar.cheers

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.backend.CoreService
import com.salazar.cheers.data.datastore.DataStoreRepository
import com.salazar.cheers.ui.CheersApp
import com.salazar.cheers.util.Constants
import com.salazar.cheers.util.StorageUtil
import com.snap.creativekit.SnapCreative
import com.snap.creativekit.exceptions.SnapMediaSizeException
import com.snap.creativekit.models.SnapPhotoContent
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    private val cheersViewModel: CheersViewModel by viewModels()
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var coreService: CoreService

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            val appSettings by dataStoreRepository.userPreferencesFlow.collectAsState(
                initial = Settings.getDefaultInstance(),
            )

            CheersApp(
                appSettings = appSettings,
                showInterstitialAd = ::showInterstitialAd,
            )
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        userConsentPolicy()
        initInterstitialAd()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                if (firebaseAuth.currentUser == null) return
                for (location in p0.locations) {
//                    GlobalScope.launch {
//                        goApi.updateLocation(longitude = location.longitude, latitude = location.latitude)
//                    }
                }
            }
        }
        StorageUtil.getSnapchatBanner(this)
    }

    override fun onResume() {
        super.onResume()

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
                if (consentInformation.consentStatus === ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(
                        this@MainActivity
                    ) {
                        loadForm(consentInformation)
                    }
                }
            }
        ) {
        }
    }

//    private fun startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationClient.requestLocationUpdates(
//            LocationRequest.create()
//                .setInterval(60000)
//                .setFastestInterval(60000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
//            locationCallback,
//            Looper.getMainLooper()
//        )
//    }
//
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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