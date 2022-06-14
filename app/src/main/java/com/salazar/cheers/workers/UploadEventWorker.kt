package com.salazar.cheers.workers

import android.app.PendingIntent
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
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.util.StorageUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import makeStatusNotification
import java.io.ByteArrayOutputStream
import java.util.*

@HiltWorker
class UploadEventWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
        val appContext = applicationContext

        makeStatusNotification("Uploading", appContext)

        val name =
            inputData.getString("NAME") ?: return Result.failure()

        val description =
            inputData.getString("DESCRIPTION") ?: ""

        val imageUri =
            inputData.getString("IMAGE_URI")

        val eventPrivacy =
            inputData.getString("EVENT_PRIVACY") ?: return Result.failure()

        val showGuestList =
            inputData.getBoolean("SHOW_GUEST_LIST", false)

        val address =
            inputData.getString("ADDRESS") ?: ""

        val locationName =
            inputData.getString("LOCATION_NAME") ?: ""

        val latitude =
            inputData.getDouble("LATITUDE", 0.0)

        val longitude =
            inputData.getDouble("LONGITUDE", 0.0)

        val startDateTime =
            inputData.getLong("START_DATETIME", 0L)

        val endDateTime =
            inputData.getLong("END_DATETIME", 0L)

        try {
            val user = userRepository.getCurrentUser()
            val event = Event(
                id = UUID.randomUUID().toString(),
                hostId = FirebaseAuth.getInstance().currentUser?.uid!!,
                hostName = user.name,
                name = name,
                address = address,
                showGuestList = showGuestList,
                description = description,
                privacy = Privacy.valueOf(eventPrivacy),
                startDate = startDateTime,
                endDate = endDateTime,
                locationName = locationName,
                latitude = latitude,
                longitude = longitude,
            )

            if (imageUri == null || imageUri == "null")
                eventRepository.uploadEvent(event)
            else {
                val photoBytes = extractImage(Uri.parse(imageUri))

                val task: Task<Uri> = StorageUtil.uploadEventImage(photoBytes)
                val downloadUrl = Tasks.await(task)

                val event = event.copy(imageUrl = downloadUrl.toString())
                eventRepository.uploadEvent(event)
            }

            return Result.success()
        } catch (throwable: Throwable) {
            Log.e("UploadEvent", throwable.message.toString())
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
        return ForegroundInfo(13, notification)
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