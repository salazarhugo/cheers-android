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
import cheers.post.v1.CreatePostRequest
import cheers.type.AudioOuterClass.Audio
import com.salazar.cheers.core.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException

@HiltWorker
class CreatePostWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository,
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val photos = inputData.getStringArray("PHOTOS") ?: emptyArray()
        if (photos.size > 8)
            return Result.failure()

        val audioUri = inputData.getString("AUDIO_URI")
        val audioAmplitudes = inputData.getIntArray("AUDIO_AMPLITUDES")
        val postType = inputData.getString("POST_TYPE") ?: return Result.failure()
        val drinkID = inputData.getString("DRINK_ID")
        val drunkenness = inputData.getInt("DRUNKENNESS", 0)
        val photoCaption = inputData.getString("PHOTO_CAPTION") ?: ""
        val locationName = inputData.getString("LOCATION_NAME") ?: ""
        val latitude = inputData.getDouble("LOCATION_LATITUDE", 0.0)
        val longitude = inputData.getDouble("LOCATION_LONGITUDE", 0.0)
        val privacy = inputData.getString("PRIVACY") ?: return Result.failure()
        val tagUserIds = inputData.getStringArray("TAG_USER_IDS") ?: emptyArray()
        val notify = inputData.getBoolean("NOTIFY", true)

        val postBuilder = CreatePostRequest.newBuilder()
            .setCaption(photoCaption)
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setDrunkenness(drunkenness.toLong())
            .setLocationName(locationName)

        if (drinkID != null) {
            postBuilder.setDrinkId(drinkID)
        }

        if (audioUri != null) {
            val bytes = extractAudio(Uri.parse(audioUri))
            if (bytes != null) {
                val uri = StorageUtil.uploadPostAudio(bytes)
                val audio = Audio.newBuilder()
                    .addAllWaveform(audioAmplitudes?.map { it.toLong() })
                    .setUrl(uri.toString())
                    .build()
                postBuilder.setAudio(audio)
            }
        }

        try {
            when (postType) {
                PostType.VIDEO -> {}
                PostType.IMAGE -> {
                    val uploadIds = mutableListOf<String>()

                    coroutineScope {
                        photos.toList().forEach { photoUri ->
                            val photoBytes = extractImage(Uri.parse(photoUri))
                            launch {
                                val uploadResult = mediaRepository.uploadMedia(photoBytes)
                                val uploadID = uploadResult.getOrNull()
                                if (uploadID != null) {
                                    uploadIds.add(uploadID)
                                }
                            }
                        }
                    }

                    val request = postBuilder
                        .addAllMediaIds(uploadIds)
                        .build()

                    postRepository.createPost(
                        request = request,
                    )
                }

                PostType.TEXT -> {
                    val request = postBuilder
                        .build()

                    postRepository.createPost(
                        request = request,
                    )
                }
            }
            return Result.success()
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
//            .setContentIntent(
//                PendingIntent.getActivity(
//                    applicationContext,
//                    2,
//                    Intent(applicationContext, MainActivity::class.java),
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            )
            .setOngoing(true)
            .setAutoCancel(true)
//            .setSmallIcon(R.drawable.cheers)
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