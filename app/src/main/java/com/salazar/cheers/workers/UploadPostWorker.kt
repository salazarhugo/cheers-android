package com.salazar.cheers.workers

import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.internal.Post
import com.salazar.cheers.util.Neo4jUtil
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import makeStatusNotification
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadPostWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val photoUriInput =
            inputData.getString("PHOTO_URI") ?: return Result.failure()

        val photoCaption =
            inputData.getString("PHOTO_CAPTION") ?: ""

        val locationName =
            inputData.getString("LOCATION_NAME") ?: ""

        val latitude =
            inputData.getDouble("LOCATION_LATITUDE", 0.0)

        val longitude =
            inputData.getDouble("LOCATION_LONGITUDE", 0.0)

        val tagUserIds =
            inputData.getStringArray("TAG_USER_IDS") ?: emptyArray()


        try {

            val photoBytes = extractImage(Uri.parse(photoUriInput))

            StorageUtil.uploadPostImage(photoBytes) { imagePath ->
                val post = Post(
                    caption = photoCaption,
                    photoPath = imagePath,
                    locationName = locationName,
                    locationLatitude = latitude,
                    locationLongitude = longitude,
                )
                Neo4jUtil.addPost(post)
                makeStatusNotification("Output is $imagePath", appContext)
            }
            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
            return Result.failure()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
        return ForegroundInfo(1337, notification)
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