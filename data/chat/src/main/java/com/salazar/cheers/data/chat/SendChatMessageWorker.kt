package com.salazar.cheers.data.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.model.EmptyChatMessage
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.data.post.repository.MediaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Date

@HiltWorker
class SendChatMessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val chatRepository: ChatRepository,
    private val mediaRepository: MediaRepository,
) : CoroutineWorker(appContext, params), CoroutineScope {

    override suspend fun doWork(): Result {
        val chatMessageID = inputData.getString("CHAT_MESSAGE_ID") ?: return Result.failure()
        val photos =
            inputData.getStringArray("IMAGES_URI") ?: return Result.failure()
        val channelId =
            inputData.getString("CHAT_CHANNEL_ID") ?: return Result.failure()
        val text = inputData.getString("TEXT")
        val replyTo = inputData.getString("REPLY_TO")

        try {

            val uploadUrls = mutableListOf<String>()

            coroutineScope {
                photos.toList().forEach { photoUri ->
                    val photoBytes = extractImage(Uri.parse(photoUri))
                    launch {
                        val media = mediaRepository.uploadMedia(photoBytes).getOrNull()
                        val mediaUrl = media?.url
                        if (mediaUrl != null) {
                            uploadUrls.add(mediaUrl)
                        }
                    }
                }
            }

            val result = chatRepository.sendChatMessage(
                chatChannelID = channelId,
                chatMessage = EmptyChatMessage.copy(
                    id = chatMessageID,
                    text = text.orEmpty(),
                    images = uploadUrls,
                    roomId = channelId,
                    status = ChatMessageStatus.SCHEDULED,
                    createTime = Date().time,
                    isSender = true,
                ),
                replyTo = replyTo,
            )

            return when (result) {
                is com.salazar.cheers.shared.util.result.Result.Error -> Result.failure()
                is com.salazar.cheers.shared.util.result.Result.Success -> Result.success()
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
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