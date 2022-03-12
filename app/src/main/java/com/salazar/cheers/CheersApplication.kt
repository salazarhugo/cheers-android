package com.salazar.cheers

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.search.MapboxSearchSdk
import com.salazar.cheers.internal.Environment
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Config
import org.neo4j.driver.GraphDatabase
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class CheersApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        initFirebase()
        initDatabase()
        initStripe()
        initMapBox()
        createNotificationChannel()
    }

    private fun initMapBox() {
        MapboxSearchSdk.initialize(
            application = this,
            accessToken = getString(R.string.mapbox_access_token),
            locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        )
    }

    private fun initStripe() {
        PaymentConfiguration.init(
            applicationContext,
            "pk_live_51KWqPTAga4Q2CELO2K93NrScmrQOQf0Pbvn0XpDXSqW4gzgXFWpMx1lnSjTfR8251B3TI4zHmQ0MqFDCYdpKD2D200KhtML5F7"
        )
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )
    }

    private fun initDatabase() {
        GraphDatabase.driver(
            Environment.DEFAULT_URL,
            AuthTokens.basic(Environment.DEFAULT_USER, Environment.DEFAULT_PASS),
            Config.builder()
                .withMaxConnectionLifetime(8, TimeUnit.MINUTES)
                .withConnectionLivenessCheckTimeout(2, TimeUnit.MINUTES).build()
        )
    }

    private fun createNotificationChannel() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val importance = NotificationManager.IMPORTANCE_HIGH

        val defaultChannel = NotificationChannel(
            getString(R.string.default_notification_channel_id),
            getString(R.string.default_notification_channel_name),
            importance
        ).apply {
            description = getString(R.string.default_notification_channel_description)
        }

        val uploadChannel = NotificationChannel(
            getString(R.string.upload_notification_channel_id),
            getString(R.string.upload_notification_channel_name),
            importance
        ).apply {
            description = getString(R.string.upload_notification_channel_description)
        }

        notificationManager.createNotificationChannel(defaultChannel)
        notificationManager.createNotificationChannel(uploadChannel)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}