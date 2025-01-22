package com.salazar.cheers.data.user.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.salazar.cheers.core.model.User
import com.salazar.cheers.data.post.repository.MEDIA_URLS_KEY
import com.salazar.cheers.data.user.R
import com.salazar.cheers.data.user.UserRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadProfilePicture @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val userRepositoryImpl: UserRepositoryImpl,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val urls = inputData.getStringArray(MEDIA_URLS_KEY)
            ?: return Result.failure()

        val avatarUrl = urls.firstOrNull() ?: return Result.failure()

        try {
            userRepositoryImpl.updateUserProfile(
                user = User().copy(picture = avatarUrl),
                updateMask = listOf("picture"),
            )

            return Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error uploading profile picture")
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