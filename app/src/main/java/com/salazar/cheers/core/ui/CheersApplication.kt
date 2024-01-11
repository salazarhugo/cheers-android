package com.salazar.cheers.core.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.salazar.ads.initializeAds
import com.salazar.cheers.BuildConfig
import com.salazar.cheers.R
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class CheersApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        initFirebase()
        initMapBox()
        createNotificationChannel()
        initializeAds(this)
    }

    private fun initMapBox() {
//        MapboxSearchSdk.initialize(
//            application = this,
//            accessToken = getString(R.string.mapbox_access_token),
////        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
//        )
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        val provider = if (BuildConfig.DEBUG)
            DebugAppCheckProviderFactory.getInstance()
        else
            PlayIntegrityAppCheckProviderFactory.getInstance()

        firebaseAppCheck.installAppCheckProviderFactory(provider)
    }

    private fun createNotificationChannel() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val importance = NotificationManager.IMPORTANCE_HIGH

        val defaultChannel = NotificationChannel(
            getString(R.string.default_notification_channel_id),
            getString(R.string.default_notification_channel_name),
            importance
        )

        val uploadsChannel = NotificationChannel(
            getString(R.string.upload_notification_channel_id),
            getString(R.string.upload_notification_channel_name),
            importance
        )
        val cheersChannel = NotificationChannel(
            getString(R.string.cheers_notification_channel_id),
            getString(R.string.cheers_notification_channel_name),
            importance
        )
        val newFollowChannel = NotificationChannel(
            getString(R.string.new_follower_notification_channel_id),
            getString(R.string.new_follower_notification_channel_name),
            importance
        )
        val newPostChannel = NotificationChannel(
            getString(R.string.new_post_notification_channel_id),
            getString(R.string.new_post_notification_channel_name),
            importance
        )
        val chatChannel = NotificationChannel(
            getString(R.string.chat_notification_channel_id),
            getString(R.string.chat_notification_channel_name),
            importance
        )

        newFollowChannel.group = getString(R.string.general_group_id)
        cheersChannel.group = getString(R.string.general_group_id)
        newPostChannel.group = getString(R.string.general_group_id)
        chatChannel.group = getString(R.string.general_group_id)

        notificationManager.createNotificationChannelGroups(
            listOf(
                NotificationChannelGroup(
                    getString(R.string.general_group_id),
                    getString(R.string.general_group_name)
                ),
                NotificationChannelGroup(
                    getString(R.string.messaging_group_id),
                    getString(R.string.messaging_group_name)
                ),
            )
        )
        notificationManager.createNotificationChannels(
            listOf(
                defaultChannel,
                uploadsChannel,
                cheersChannel,
                newFollowChannel,
                newPostChannel,
                chatChannel,
            )
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}