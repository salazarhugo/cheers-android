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
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.util.StorageUtil
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

        try {
            when (postType) {
                PostType.VIDEO -> {
//                    val videoUri = Uri.parse(mediaUri) ?: return Result.failure()
//
//                    val ref = StorageUtil.currentUserRef.child("posts/${UUID.randomUUID()}")
//                    val uploadTask = ref.putFile(videoUri)
//
//                    uploadTask.addOnProgressListener {
//                        val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
//                        setProgressAsync(workDataOf("Progress" to progress))
//                    }.continueWithTask {
//                        ref.downloadUrl
//                    }.addOnSuccessListener { downloadUri ->
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////                            val videoThumbnail: Bitmap = ThumbnailUtils.createVideoThumbnail(File(videoUri.path), Size(120, 120), null)
//                            val videoThumbnail: Bitmap = appContext.contentResolver.loadThumbnail(
//                                videoUri,
//                                Size(1080, 1080),
//                                null
//                            )
//                            val thumbnailBytes = extractBitmap(videoThumbnail)
//
//                            StorageUtil.uploadPostImage(thumbnailBytes) { thumbnailUrl ->
//                                val post = Post(
//                                    name = name,
//                                    type = postType,
//                                    authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
//                                    caption = photoCaption,
//                                    videoUrl = downloadUri.toString(),
//                                    videoThumbnailUrl = thumbnailUrl,
//                                    locationName = locationName,
//                                    locationLatitude = latitude,
//                                    locationLongitude = longitude,
//                                    privacy = privacy,
//                                    allowJoin = allowJoin,
//                                    tagUsersId = tagUserIds.toList(),
//                                )
//                                GlobalScope.launch {
//                                    Neo4jUtil.addPost(post)
//                                }
//                                makeStatusNotification("Successfully uploaded", appContext)
//                            }
//                        } else {
//                            val post = Post(
//                                name = name,
//                                type = postType,
//                                authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
//                                caption = photoCaption,
//                                videoUrl = downloadUri.toString(),
//                                locationName = locationName,
//                                locationLatitude = latitude,
//                                locationLongitude = longitude,
//                                privacy = privacy,
//                                allowJoin = allowJoin,
//                                tagUsersId = tagUserIds.toList()
//                            )
//                            GlobalScope.launch {
//                                Neo4jUtil.addPost(post)
//                            }
//                            makeStatusNotification("Successfully uploaded", appContext)
//                        }
//                    }
//
                }
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


                    val post = PostOuterClass.Post.newBuilder()
                        .setType(PostOuterClass.PostType.IMAGE)
                        .setCaption(photoCaption)
                        .addAllPhotos(downloadUrls)
                        .setDrunkenness(drunkenness.toLong())
                        .setDrink(beverage)
                        .setLocationName(locationName)
                        .build()

                    postRepository.createPost(post = post)

                }
                PostType.TEXT -> {
                    val post = PostOuterClass.Post.newBuilder()
                        .setType(PostOuterClass.PostType.TEXT)
                        .setCaption(photoCaption)
                        .setDrunkenness(drunkenness.toLong())
                        .setDrink(beverage)
                        .setLocationName(locationName)
                        .build()

                    postRepository.createPost(post = post)
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