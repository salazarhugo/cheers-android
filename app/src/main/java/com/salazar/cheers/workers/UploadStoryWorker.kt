package com.salazar.cheers.workers

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.data.repository.StoryRepository
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


@HiltWorker
class UploadStoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val storyRepository: StoryRepository,
) : CoroutineWorker(appContext, params) {

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val appContext = applicationContext
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

        val photoUri =
            inputData.getString("PHOTO") ?: return Result.failure()

        val storyType =
            inputData.getString("STORY_TYPE") ?: return Result.failure()

        val locationName =
            inputData.getString("LOCATION_NAME") ?: ""

        var latitude: Double? =
            inputData.getDouble("LOCATION_LATITUDE", 0.0)
        if (latitude == 0.0) latitude = null

        var longitude: Double? =
            inputData.getDouble("LOCATION_LONGITUDE", 0.0)
        if (longitude == 0.0) longitude = null

        var altitude: Double? =
            inputData.getDouble("ALTITUDE", 0.0)
        if (altitude == 0.0) altitude = null

        val privacy =
            inputData.getString("PRIVACY") ?: return Result.failure()

        val tagUserIds =
            inputData.getStringArray("TAG_USER_IDS") ?: emptyArray()

        try {

            val photoBytes = extractImage(Uri.parse(photoUri))
            val uri = StorageUtil.uploadStoryImage(photoBytes)
            val downloadUrl = uri.toString()

            try {
                mFusedLocationClient?.lastLocation?.await()?.let {
                    longitude = it.longitude
                    latitude = it.latitude
                    altitude = it.altitude
                }
            } catch (e: Exception) {
                Log.e("Location", "Couldn't get last location")
            }

            val story = Story(
                type = storyType,
                photoUrl = downloadUrl,
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                privacy = privacy,
                tagUsersId = tagUserIds.toList()
            )

            storyRepository.addStory(story)

            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
            return Result.failure()
        }
    }

//    override suspend fun getForegroundInfo(): ForegroundInfo {
//        val notification = NotificationCompat.Builder(
//            applicationContext,
//            applicationContext.getString(R.string.upload_notification_channel_id)
//        )
//            .setContentIntent(
//                PendingIntent.getActivity(
//                    applicationContext,
//                    2,
//                    Intent(applicationContext, MainActivity::class.java),
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            )
//            .setOngoing(true)
//            .setAutoCancel(true)
//            .setSmallIcon(R.drawable.cheers)
//            .setOnlyAlertOnce(true)
//            .setPriority(NotificationCompat.PRIORITY_MIN)
//            .setLocalOnly(true)
//            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
//            .setContentText("Updating widget")
//            .build()
//
//        return ForegroundInfo(42, notification)
//    }

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