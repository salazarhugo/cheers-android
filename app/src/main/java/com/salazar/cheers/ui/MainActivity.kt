package com.salazar.cheers.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.Theme
import com.salazar.cheers.core.analytics.AnalyticsHelper
import com.salazar.cheers.core.analytics.LocalAnalyticsHelper
import com.salazar.cheers.core.ui.CheersUiState
import com.salazar.cheers.core.ui.CheersViewModel
import com.salazar.cheers.data.ads.AdsConsentManager
import com.salazar.cheers.data.ads.AdsManager
import com.salazar.cheers.shared.util.LocalActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), FirebaseAuth.AuthStateListener {

    private val updateType = AppUpdateType.IMMEDIATE
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    @Inject
    lateinit var dataStoreRepository: com.salazar.cheers.data.user.datastore.DataStoreRepository

    @Inject
    lateinit var friendshipRepository: com.salazar.cheers.data.friendship.FriendshipRepository

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var adsManager: AdsManager

    @Inject
    lateinit var adsConsentManager: AdsConsentManager

    private val viewModel: CheersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: CheersUiState by mutableStateOf(CheersUiState.Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations, and go edge-to-edge
        // This also sets up the initial system bar style based on the platform theme
        enableEdgeToEdge()

        checkForAppUpdates()

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)

            // Update the edge to edge configuration to match the theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                        detectDarkMode = { darkTheme },
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = lightScrim,
                        darkScrim = darkScrim,
                        detectDarkMode = { darkTheme }
                    ),
                )
                onDispose {}
            }

            val appState: CheersAppState = rememberCheersAppState()

            CompositionLocalProvider(
                LocalActivity provides this,
                LocalAnalyticsHelper provides analyticsHelper,
            ) {
                CheersApp(
                    appState = appState,
                    darkTheme = darkTheme,
                    onRewardedAdClick = {
                        adsManager.showRewardedAd(this)
                    }
                )
            }
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        adsConsentManager.gatherUserConsent(this, {
            if (adsConsentManager.canRequestAds) {
                adsManager.init(this)
            }
        })
        adsManager.init(this)
    }

    override fun onResume() {
        super.onResume()

        try {
            Log.d("INTENT", intent.data.toString())
            val data = intent.data ?: return
            val auth = Firebase.auth
            if (!auth.isSignInWithEmailLink(data.toString()))
                return
            val continueUrl = data.getQueryParameter("continueUrl") ?: return

            val encodedUrl = data.toString().encodeToByteArray().decodeToString()

            val deepLinkIntent = Intent(
                Intent.ACTION_VIEW,
                "$continueUrl/$encodedUrl".toUri(),
                this,
                MainActivity::class.java,
            )

            startActivity(deepLinkIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
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
            viewModel.onNewMessage()
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        Log.i("AUTH", p0.currentUser?.uid.toString())
        viewModel.onAuthChange(p0)
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
@Composable
private fun shouldUseDarkTheme(
    uiState: CheersUiState,
): Boolean = when (uiState) {
    is CheersUiState.Loading -> isSystemInDarkTheme()
    is CheersUiState.Initialized -> when (uiState.settings.theme) {
        Theme.SYSTEM_DEFAULT, Theme.UNRECOGNIZED -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }
}
