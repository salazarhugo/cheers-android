package com.salazar.cheers.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.salazar.cheers.util.FirestoreUtil


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(newRegistrationToken: String) {
        super.onNewToken(newRegistrationToken)

        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(newRegistrationToken)
    }

    private fun addTokenToFirestore(newRegistrationToken: String?) {
        if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

        FirestoreUtil.getFCMRegistrationTokens { tokens ->
            if (tokens.contains(newRegistrationToken))
                return@getFCMRegistrationTokens

            tokens.add(newRegistrationToken)
            FirestoreUtil.setFCMRegistrationTokens(tokens)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            //TODO: Show notification if we're not online
            Log.d("FCM", remoteMessage.data.toString())
        }
    }
}