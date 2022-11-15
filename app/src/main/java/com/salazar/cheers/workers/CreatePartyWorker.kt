package com.salazar.cheers.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cheers.type.PartyOuterClass
import cheers.type.PostOuterClass
import cheers.type.PrivacyOuterClass
import com.google.firebase.auth.FirebaseAuth
import com.google.protobuf.Timestamp
import com.google.type.LatLng
import com.salazar.cheers.ui.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.data.repository.party.PartyRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.Privacy
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import makeStatusNotification
import java.io.ByteArrayOutputStream
import java.util.*

@HiltWorker
class CreatePartyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val partyRepository: PartyRepository,
    private val userRepository: UserRepository,
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val name =
            inputData.getString("NAME") ?: return Result.failure()

        val description =
            inputData.getString("DESCRIPTION") ?: ""

        val imageUri =
            inputData.getString("IMAGE_URI")

        val eventPrivacy =
            inputData.getString("EVENT_PRIVACY") ?: return Result.failure()

        val showGuestList =
            inputData.getBoolean("SHOW_GUEST_LIST", false)

        val address =
            inputData.getString("ADDRESS") ?: ""

        val locationName =
            inputData.getString("LOCATION_NAME") ?: ""

        val latitude =
            inputData.getDouble("LATITUDE", 0.0)

        val longitude =
            inputData.getDouble("LONGITUDE", 0.0)

        val startDateTime =
            inputData.getLong("START_DATETIME", 0L)

        val endDateTime =
            inputData.getLong("END_DATETIME", 0L)

        try {
            val user = userRepository.getCurrentUser()
            val uid = FirebaseAuth.getInstance().currentUser?.uid!!

            val party = PartyOuterClass.Party.newBuilder()
                .setName(name)
                .setDescription(description)
                .setAddress(address)
                .setStartDate(Timestamp.newBuilder().setSeconds(startDateTime/1000))
                .setEndDate(Timestamp.newBuilder().setSeconds(startDateTime/1000))
                .setLocationName(locationName)
                .setLatlng(LatLng.newBuilder().setLatitude(latitude).setLongitude(longitude).build())
                .setPrivacy(PrivacyOuterClass.Privacy.PUBLIC)
                .build()

            partyRepository.createParty(party = party)

            return Result.success()
        } catch (throwable: Throwable) {
            Log.e("UploadEvent", throwable.message.toString())
            return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.cheers)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setLocalOnly(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentText("Updating widget")
            .build()
        return ForegroundInfo(13, notification)
    }
}