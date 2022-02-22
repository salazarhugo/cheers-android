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
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import makeStatusNotification
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadPostWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val photos =
            inputData.getStringArray("PHOTOS") ?: emptyArray()

        if (photos.size > 5) return Result.failure()

        val postType =
            inputData.getString("POST_TYPE") ?: return Result.failure()

        val name =
            inputData.getString("NAME") ?: ""

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

        val allowJoin =
            inputData.getBoolean("ALLOW_JOIN", true)

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

                        val post = Post(
                            name = name,
                            type = postType,
                            caption = photoCaption,
                            photos = downloadUrls,
                            locationName = locationName,
                            locationLatitude = latitude,
                            locationLongitude = longitude,
                            privacy = privacy,
                            allowJoin = allowJoin,
                            tagUsersId = tagUserIds.toList()
                        )

                        GlobalScope.launch {
                            Neo4jUtil.addPost(post)
                        }
                        makeStatusNotification("Successfully uploaded", appContext)
                    }

                }
                PostType.TEXT -> {
                    val post = Post(
                        name = name,
                        type = postType,
                        caption = photoCaption,
                        locationName = locationName,
                        locationLatitude = latitude,
                        locationLongitude = longitude,
                        privacy = privacy,
                        allowJoin = allowJoin,
                        tagUsersId = tagUserIds.toList()
                    )
                    Neo4jUtil.addPost(post)
                    makeStatusNotification("Successfully uploaded", appContext)
                }
            }
//            setProgressAsync(workDataOf("Progress" to 0.0))
//            delay(2000)
//            setProgressAsync(workDataOf("Progress" to 50.0))
//            delay(2000)
//            setProgressAsync(workDataOf("Progress" to 100.0))
//            delay(2000)
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