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
import cheers.type.PrivacyOuterClass
import cheers.type.StoryOuterClass
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.salazar.cheers.data.repository.story.StoryRepository
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

            val story = StoryOuterClass.Story.newBuilder()
                .setType(StoryOuterClass.Story.StoryType.IMAGE)
                .setPhoto(downloadUrl)
                .setLocationName(locationName)
                .setPrivacy(PrivacyOuterClass.Privacy.FRIENDS)
                .build()

            val result = storyRepository.createStory(story)

            return when (result.isSuccess) {
                true  -> Result.success()
                false -> Result.failure()
            }
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
            return Result.failure()
        }
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