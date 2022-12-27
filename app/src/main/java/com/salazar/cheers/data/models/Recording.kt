package com.salazar.cheers.data.models

import android.media.MediaMetadataRetriever
import android.os.Parcelable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

//@Parcelize
//data class Recording(
//    val duration: String,
//    val readableDate: String,
//    val readableDayTime: String,
//    val date: Date,
//    val path: String
//) : Parcelable


fun convertDurationToString(duration: Int): String = String.format(
    "%02d:%02d",
    TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
    TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
)

fun generateRecordingName(path: String?): String {
    return "${path}/Macaw-${
        SimpleDateFormat("ddMMyyyy-HHmmss", Locale.getDefault()).format(
            Calendar.getInstance().time
        )
    }.m4a"
}