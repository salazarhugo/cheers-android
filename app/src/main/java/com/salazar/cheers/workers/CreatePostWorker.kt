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
import cheers.type.PostOuterClass
import com.salazar.cheers.ui.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.post.data.repository.PostRepository
import com.salazar.cheers.core.data.internal.PostType
import com.salazar.cheers.core.data.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@HiltWorker
class CreatePostWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val postRepository: PostRepository,
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val appContext = applicationContext

        val photos =
            inputData.getStringArray("PHOTOS") ?: emptyArray()

        if (photos.size > 5) return Result.failure()

        val postType =
            inputData.getString("POST_TYPE") ?: return Result.failure()

        val beverage =
            inputData.getString("BEVERAGE") ?: return Result.failure()

        val drunkenness =
            inputData.getInt("DRUNKENNESS", 0)

        val photoCaption =
            inputData.getString("PHOTO_CAPTION") ?: ""

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

        val notify =
            inputData.getBoolean("NOTIFY", true)

        val postBuilder = PostOuterClass.Post.newBuilder()
            .setCaption(photoCaption)
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setDrunkenness(drunkenness.toLong())
            .setDrink(beverage)
            .setLocationName(locationName)

        try {
            when (postType) {
                PostType.VIDEO -> {}
                PostType.IMAGE -> {
                    val downloadUrls = mutableListOf<String>()

                    coroutineScope {
                        photos.toList().forEach { photoUri ->
                            val photoBytes = extractImage(Uri.parse(photoUri))
                            launch {
                                val uri = StorageUtil.uploadPostImage(photoBytes)
                                downloadUrls.add(uri.toString())
                            }
                        }
                    }


                    val post = postBuilder
                        .setType(PostOuterClass.PostType.IMAGE)
                        .addAllPhotos(downloadUrls)
                        .build()

                    postRepository.createPost(
                        post = post,
                        sendNotificationToFriends = notify,
                    )
                }
                PostType.TEXT -> {
                    val post = postBuilder
                        .setType(PostOuterClass.PostType.TEXT)
                        .build()

                    postRepository.createPost(
                        post = post,
                        sendNotificationToFriends = notify,
                    )
                }
            }
            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error uploading post")
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
}