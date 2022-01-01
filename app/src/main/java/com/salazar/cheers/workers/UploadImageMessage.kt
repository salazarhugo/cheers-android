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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.UploadTask
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.internal.ImageMessage
import com.salazar.cheers.internal.MessageType
import com.salazar.cheers.util.FirestoreChat
import com.salazar.cheers.util.Neo4jUtil
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import makeStatusNotification
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadImageMessage @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val imagesUri =
            inputData.getStringArray("IMAGES_URI") ?: return Result.failure()

        val channelId =
            inputData.getString("CHANNEL_ID") ?: return Result.failure()

        val fullName =
            inputData.getString("FULL_NAME") ?: return Result.failure()

        val username =
            inputData.getString("USERNAME") ?: return Result.failure()

        val profilePicturePath =
            inputData.getString("PROFILE_PICTURE_PATH") ?: return Result.failure()

        try {

            // TODO (Implement multiple images upload)
//            val imagesPath = mutableListOf<String>()
//            imagesUri.forEach {
//            }

            val first = if(imagesUri.isNotEmpty()) imagesUri.first() else return Result.failure()

            val photoBytes = extractImage(Uri.parse(first))

            StorageUtil.uploadMessageImage(photoBytes) {
                val imageMessage =
                    ImageMessage().copy(
                        imagesPath = listOf(it),
                        senderId = FirebaseAuth.getInstance().currentUser?.uid!!,
                        senderName = fullName,
                        senderUsername = username,
                        chatChannelId = channelId,
                        senderProfilePicturePath = profilePicturePath,
                        type = MessageType.IMAGE,
                        recipientId = ""
                    )

                FirestoreChat.sendMessage(imageMessage, channelId)
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