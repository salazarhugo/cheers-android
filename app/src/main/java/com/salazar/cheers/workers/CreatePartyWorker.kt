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
import cheers.party.v1.CreatePartyRequest
import cheers.party.v1.Geolocation
import com.salazar.cheers.R
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.data.post.repository.MediaRepository
import com.salazar.cheers.shared.data.mapper.toPrivacyPb
import com.salazar.cheers.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import makeStatusNotification
import java.io.ByteArrayOutputStream

@HiltWorker
class CreatePartyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val partyRepository: com.salazar.cheers.data.party.data.repository.PartyRepository,
    private val mediaRepository: MediaRepository,
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val name =
            inputData.getString("NAME") ?: return Result.failure()

        val description =
            inputData.getString("DESCRIPTION") ?: ""

        val bannerUri =
            inputData.getString("IMAGE_URI")

        val eventPrivacy =
            inputData.getString("EVENT_PRIVACY") ?: return Result.failure()
        val privacy =
            Privacy.entries.firstOrNull() { it.name == eventPrivacy } ?: return Result.failure()

        val showGuestList =
            inputData.getBoolean("SHOW_GUEST_LIST", false)

        val city =
            inputData.getString("CITY") ?: ""

        val address =
            inputData.getString("ADDRESS") ?: ""

        val latitude =
            inputData.getDouble("LATITUDE", 0.0)

        val longitude =
            inputData.getDouble("LONGITUDE", 0.0)

        val startDateTime =
            inputData.getLong("START_DATETIME", 0L)

        val endDateTime =
            inputData.getLong("END_DATETIME", 0L)

        try {
            val geolocation = Geolocation.newBuilder()
                .setCity(city)
                .setAddress(address)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .build()

            val request = CreatePartyRequest.newBuilder()
                .setName(name)
                .setDescription(description)
                .setGeolocation(geolocation)
                .setStartDate(startDateTime)
                .setEndDate(endDateTime)
                .setPrivacy(privacy.toPrivacyPb())

            if (bannerUri?.isNotBlank() == true) {
                val photoBytes = extractImage(Uri.parse(bannerUri))
                val media =
                    mediaRepository.uploadMedia(photoBytes).getOrNull() ?: return Result.failure()
                request.setBannerUrl(media.url)
            }

            return partyRepository.createParty(request = request.build()).fold(
                onFailure = {
                    it.printStackTrace()
                    Result.failure()
                },
                onSuccess = { Result.success() }
            )
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

    private fun extractImage(path: Uri): ByteArray {
        val source: ImageDecoder.Source =
            ImageDecoder.createSource(applicationContext.contentResolver, path)
        val selectedImageBmp: Bitmap = ImageDecoder.decodeBitmap(source)

        val outputStream = ByteArrayOutputStream()
        selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        return outputStream.toByteArray()
    }

}