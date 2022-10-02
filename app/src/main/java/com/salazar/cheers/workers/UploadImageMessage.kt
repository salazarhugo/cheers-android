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
import androidx.work.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.salazar.cheers.ui.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadImageMessage @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val chatRepository: ChatRepository,
) : CoroutineWorker(appContext, params), CoroutineScope {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

//        makeStatusNotification("Uploading", appContext)

        val imagesUri =
            inputData.getStringArray("IMAGES_URI") ?: return Result.failure()

        val channelId =
            inputData.getString("CHANNEL_ID") ?: return Result.failure()

        try {

            val first = if (imagesUri.isNotEmpty()) imagesUri.first() else return Result.failure()

            val photoBytes = extractImage(Uri.parse(first))

            val task: Task<Uri> = StorageUtil.uploadMessageImage(photoBytes)
            val downloadUrl = Tasks.await(task)

            chatRepository.sendImageMessage(
                channelId = channelId,
                photoUrl = downloadUrl.toString(),
            )

            val output: Data = workDataOf("result" to downloadUrl)

            return Result.success(output)
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
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