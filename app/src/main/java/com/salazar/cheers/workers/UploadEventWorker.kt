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
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.internal.Event
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import makeStatusNotification
import java.io.ByteArrayOutputStream
import java.util.*

@HiltWorker
class UploadEventWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val name =
            inputData.getString("NAME") ?: ""

        val description =
            inputData.getString("DESCRIPTION") ?: ""

        val imageUri =
            inputData.getString("IMAGE_URI")

        val eventType =
            inputData.getString("EVENT_TYPE") ?: return Result.failure()

        val locationName =
            inputData.getString("LOCATION_NAME") ?: ""

        val latitude =
            inputData.getDouble("LOCATION_LATITUDE", 0.0)

        val longitude =
            inputData.getDouble("LOCATION_LONGITUDE", 0.0)

        val showOnMap =
            inputData.getBoolean("SHOW_ON_MAP", true)

        val participants =
            inputData.getStringArray("PARTICIPANTS") ?: emptyArray()

        val startDateTime =
            inputData.getString("START_DATETIME") ?: ""

        val endDateTime =
            inputData.getString("END_DATETIME") ?: ""

        try {
            val event = Event(
                id = UUID.randomUUID().toString(),
                host = FirebaseAuth.getInstance().currentUser?.uid!!,
                name = name,
                description = description,
                type = eventType,
                startDate = startDateTime,
                endDate = endDateTime,
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
                showOnMap = showOnMap,
            )

            if (imageUri == null || imageUri == "null")
                Neo4jUtil.addEvent(event)
            else {
                val photoBytes = extractImage(Uri.parse(imageUri))
                StorageUtil.uploadEventImage(photoBytes) { downloadUrl ->
                    Neo4jUtil.addEvent(event.copy(imageUrl = downloadUrl))
                }
            }

            return Result.success()
        } catch (throwable: Throwable) {
            Log.e("UploadEvent", "Error uploading event")
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

    private fun extractBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        return outputStream.toByteArray()
    }
}