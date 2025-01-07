package com.salazar.cheers.data.post.repository

import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException

const val MEDIA_URI_KEY = "MEDIA_URI"
const val MEDIA_URLS_KEY = "MEDIA_URLS"

@HiltWorker
class UploadMediaWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository,
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val medias = inputData.getStringArray(MEDIA_URI_KEY)
            ?: return Result.failure()

        try {
            val uploadIds = mutableListOf<String>()

            coroutineScope {
                medias.toList().forEach { photoUri ->
                    val photoBytes = extractImage(Uri.parse(photoUri))
                    launch {
                        val media = mediaRepository.uploadMedia(photoBytes).getOrNull()
                        val mediaID = media?.url
                        if (mediaID != null) {
                            uploadIds.add(mediaID)
                        }
                    }
                }
            }

            val outputData = workDataOf(
                MEDIA_URLS_KEY to uploadIds.toTypedArray(),
            )

            return Result.success(outputData)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Log.e(TAG, "Error uploading post")
            return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(
            applicationContext,
            "UPLOAD_CHANNEL",
        )
            .setOngoing(true)
            .setAutoCancel(true)
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

    private fun extractAudio(uri: Uri): ByteArray? {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.toByteArray()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}