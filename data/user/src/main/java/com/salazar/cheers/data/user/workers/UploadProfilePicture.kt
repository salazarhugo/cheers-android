package com.salazar.cheers.data.user.workers

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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.salazar.cheers.core.util.StorageUtil
import com.salazar.cheers.data.user.R
import com.salazar.cheers.data.user.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.ByteArrayOutputStream

@HiltWorker
class UploadProfilePicture @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val userRepository: UserRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        val photoUriInput =
            inputData.getString("PHOTO_URI") ?: return Result.failure()

        try {

            val photoBytes = extractImage(Uri.parse(photoUriInput))

            val task: Task<Uri> = StorageUtil.uploadProfilePhoto(photoBytes)
            val downloadUrl = Tasks.await(task)

            val user = userRepository.getCurrentUser()
            userRepository.updateUser(user.copy(picture = downloadUrl.toString()))

            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error applying blur")
            return Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(
            applicationContext,
            "DEFAULT_CHANNEL"
        )
//            .setContentIntent(
//                PendingIntent.getActivity(
//                    applicationContext,
//                    0,
//                    Intent(applicationContext, MainActivity::class.java),
//                    PendingIntent.FLAG_IMMUTABLE
//                )
//            )
            .setOngoing(false)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.cheers_logo)
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