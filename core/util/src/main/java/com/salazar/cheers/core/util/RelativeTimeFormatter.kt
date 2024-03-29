package com.salazar.cheers.core.util

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun relativeTimeFormatterMilli(
    milliSeconds: Long,
): AnnotatedString {
    return relativeTimeFormatter(seconds = milliSeconds / 1000)
}

@Composable
fun relativeTimeFormatter(
    seconds: Long,
): AnnotatedString {

    return buildAnnotatedString {

        val elapsedSeconds = abs(System.currentTimeMillis() / 1000 - seconds)

        val res = when {
            elapsedSeconds < 60 -> "just now"
            elapsedSeconds < 60 * 60 -> "${elapsedSeconds / 60}m"
            elapsedSeconds < 60 * 60 * 24 -> "${elapsedSeconds / (60 * 60)}h"
            elapsedSeconds < 60 * 60 * 24 * 7 -> "${elapsedSeconds / (60 * 60 * 24)}d"
            elapsedSeconds < 60 * 60 * 24 * 30 -> "${elapsedSeconds / (60 * 60 * 24 * 7)}w"
            elapsedSeconds < 60 * 60 * 24 * 365 -> "${elapsedSeconds / (60 * 60 * 24 * 30)}mo"
            else -> "${elapsedSeconds / (60 * 60 * 24 * 365)}y"
        }

        append(res)
    }
}

/*
 *  timestamp in seconds
 */
@Composable
fun startDateFormatter(
    timestamp: Long,
): AnnotatedString {
    return buildAnnotatedString {
        val date = Date(timestamp * 1000)

        if (DateUtils.isToday(timestamp * 1000)) {
            append("Today")
            return@buildAnnotatedString
        }

        if (DateUtils.isToday(timestamp * 1000 - DateUtils.DAY_IN_MILLIS)) {
            append("Tomorrow")
            return@buildAnnotatedString
        }

        if (Date().time > timestamp * 1000 - 5 * DateUtils.DAY_IN_MILLIS) {
            val res = SimpleDateFormat("EEEE").format(date)
            append(res)
            return@buildAnnotatedString
        }

        val res = SimpleDateFormat("dd MMMM yyyy").format(date)
        append(res)
        return@buildAnnotatedString
    }
}

/*
 *  timestamp in seconds
 */
@Composable
fun timeFormatter(
    timestamp: Long,
): AnnotatedString {

    return buildAnnotatedString {
        val date = Date(timestamp * 1000)
        val res = SimpleDateFormat("HH:mm").format(date)
        append(res)
        return@buildAnnotatedString
    }
}

/*
 *  timestamp in seconds
 */
@Composable
fun dateTimeFormatter(
    timestamp: Long,
): AnnotatedString {

    return buildAnnotatedString {
        val date = Date(timestamp * 1000)

        val time = SimpleDateFormat("HH:mm").format(date)

        if (DateUtils.isToday(timestamp * 1000)) {
            append("${stringResource(id = R.string.today)}, $time")
            return@buildAnnotatedString
        }

        if (DateUtils.isToday(timestamp * 1000 - DateUtils.DAY_IN_MILLIS)) {
            append("${stringResource(id = R.string.tomorrow)}, $time")
            return@buildAnnotatedString
        }

        if (Date().time > timestamp * 1000 - 5 * DateUtils.DAY_IN_MILLIS) {
            val res = SimpleDateFormat("EEEE, HH:mm", Locale.getDefault()).format(date)
            append(res)
            return@buildAnnotatedString
        }

        val res = SimpleDateFormat("EEE dd MMM, HH:mm", Locale.getDefault()).format(date)
        append(res)
        return@buildAnnotatedString
    }
}
