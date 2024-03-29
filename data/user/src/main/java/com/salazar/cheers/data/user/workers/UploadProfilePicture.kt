package com.salazar.cheers.data.user.workers

import android.content.ContentValues.TAG
import android.content.Context
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
import com.salazar.cheers.core.util.Utils.extractImage
import com.salazar.cheers.data.user.R
import com.salazar.cheers.data.user.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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
            val photoBytes = extractImage(Uri.parse(photoUriInput), applicationContext)

            val task: Task<Uri> = StorageUtil.uploadProfilePhoto(photoBytes)
            val downloadUrl = Tasks.await(task)

            val user = userRepository.getCurrentUser()
            userRepository.updateUserProfile(user.copy(picture = downloadUrl.toString()))

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
}