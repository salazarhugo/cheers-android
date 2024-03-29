package com.salazar.cheers.data.post.repository

import androidx.work.WorkManager
import cheers.media.v1.MediaServiceGrpcKt
import cheers.media.v1.UploadMediaRequest
import com.google.protobuf.ByteString
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val mediaService: MediaServiceGrpcKt.MediaServiceCoroutineStub,
    private val workManager: WorkManager,
) {

    suspend fun uploadMedia(bytes: ByteArray): Result<String> {
        val byteString = ByteString.copyFrom(bytes)

        try {
            val request = UploadMediaRequest.newBuilder()
                .setUploadId(Date().time)
                .setMediaType("")
                .setChunk(byteString)
                .build()

            val response = mediaService.uploadMedia(
                request = request,
            )

            return Result.success(response.mediaId)
        } catch (e: Exception) {
            e.printStackTrace()
            return  Result.failure(e)
        }
    }
}