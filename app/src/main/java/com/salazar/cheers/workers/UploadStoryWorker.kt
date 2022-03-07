package com.salazar.cheers.workers

import android.app.PendingIntent
import android.content.ContentValues.TAG
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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.entities.StoryResponse
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import makeStatusNotification
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadStoryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val photos =
            inputData.getStringArray("PHOTOS") ?: emptyArray()

        if (photos.size > 5) return Result.failure()

        val storyType =
            inputData.getString("STORY_TYPE") ?: return Result.failure()

        val locationName =
            inputData.getString("LOCATION_NAME") ?: ""

        val latitude =
            inputData.getDouble("LOCATION_LATITUDE", 0.0)

        val longitude =
            inputData.getDouble("LOCATION_LONGITUDE", 0.0)

        val privacy =
            inputData.getString("PRIVACY") ?: return Result.failure()

        val tagUserIds =
            inputData.getStringArray("TAG_USER_IDS") ?: emptyArray()

        try {
            val tasks: MutableList<Deferred<Task<*>>> = mutableListOf()

            coroutineScope {
                photos.toList().forEach { photoUri ->
                    val photoBytes = extractImage(Uri.parse(photoUri))
                    val task = async {
                        StorageUtil.uploadPostImage2(photoBytes)
                    }
                    tasks.add(task)
                }
            }

            Tasks.whenAllComplete(tasks.awaitAll()).addOnSuccessListener {
                val downloadUrls = mutableListOf<String>()

                it.forEach { task ->
                    task.addOnSuccessListener { downloadUrl ->
                        downloadUrls.add(downloadUrl.toString())
                    }
                }

                val story = StoryResponse(
                    type = storyType,
                    photos = downloadUrls,
                    locationName = locationName,
                    locationLatitude = latitude,
                    locationLongitude = longitude,
                    privacy = privacy,
                    tagUsersId = tagUserIds.toList()
                )

                GlobalScope.launch {
                    Neo4jUtil.addStory(story)
                }

                makeStatusNotification("Successfully uploaded", appContext)
            }
            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
            return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.upload_notification_channel_id)
        )
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    2,
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

        return ForegroundInfo(42, notification)
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