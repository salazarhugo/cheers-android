package com.salazar.cheers.feature.chat.data.worker

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.salazar.cheers.feature.chat.data.repository.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import java.io.ByteArrayOutputStream

//@HiltWorker
class UploadImageMessage @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val chatRepository: ChatRepository,
) : CoroutineWorker(appContext, params), CoroutineScope {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        val imagesUri =
            inputData.getStringArray("IMAGES_URI") ?: return Result.failure()

        val channelId =
            inputData.getString("CHANNEL_ID") ?: return Result.failure()

        try {

            val first = if (imagesUri.isNotEmpty()) imagesUri.first() else return Result.failure()

            val photoBytes = extractImage(Uri.parse(first))

//            val task: Task<Uri> = StorageUtil.uploadMessageImage(photoBytes)
//            val downloadUrl = Tasks.await(task)

//            chatRepository.sendImageMessage(
//                channelId = channelId,
//                photoUrl = downloadUrl.toString(),
//            )
//
//            val output: Data = workDataOf("result" to downloadUrl)

            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
            return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")
            .setOngoing(true)
            .setAutoCancel(true)
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