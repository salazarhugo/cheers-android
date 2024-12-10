package com.salazar.cheers.data.post.repository

import cheers.media.v1.MediaServiceGrpcKt
import cheers.media.v1.UploadMediaRequest
import com.google.protobuf.ByteString
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.shared.data.mapper.toMedia
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val mediaService: MediaServiceGrpcKt.MediaServiceCoroutineStub,
) {
    suspend fun uploadMedia(bytes: ByteArray): Result<Media> {
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

            return Result.success(response.media.toMedia())
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }
}