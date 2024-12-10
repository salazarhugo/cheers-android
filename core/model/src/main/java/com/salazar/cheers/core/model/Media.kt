package com.salazar.cheers.core.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

sealed interface Media {
    val id: String
    val url: String

    data class Video(
        override val id: String = String(),
        override val url: String = String(),
        val uri: Uri,
        val hasSound: Boolean,
    ) : Media

    data class Image(
        override val id: String = String(),
        override val url: String = String(),
        val uri: Uri = Uri.EMPTY,
    ) : Media
}

fun Uri.toMedia(context: Context): Media {
    val mime = MimeTypeMap.getSingleton()
    val cR: ContentResolver = context.contentResolver
    val extension = mime.getExtensionFromMimeType(cR.getType(this))

    return when (extension) {
        "mp4" -> {
            Media.Video(
                uri = this,
                hasSound = true,
            )
        }

        "jpg", "png" -> {
            Media.Image(
                uri = this
            )
        }

        else -> {
            Media.Image(uri = this)
        }
    }
}

fun List<Uri>.toMedia(context: Context): List<Media> {
    return map { uri ->
        uri.toMedia(context)
    }
}