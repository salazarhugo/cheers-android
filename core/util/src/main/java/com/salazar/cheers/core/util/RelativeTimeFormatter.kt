package com.salazar.cheers.core.util

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun relativeTimeFormatterMilli(
    value: Long,
    initialTimeMillis: Long = System.currentTimeMillis(),
): AnnotatedString {
    return relativeTimeFormatter(
        seconds = value / 1000,
        initialTimeMillis = initialTimeMillis,
    )
}

const val JUST_NOW_MILLIS = 2 * 1000L

fun isJustNow(timeMillis: Long): Boolean {
    return (Date().time - timeMillis) < JUST_NOW_MILLIS
}

@Composable
fun relativeTimeFormatter(
    seconds: Long,
    initialTimeMillis: Long = System.currentTimeMillis(),
): AnnotatedString {

    return buildAnnotatedString {

        val elapsedSeconds = abs(initialTimeMillis / 1000 - seconds)
        val elapsedMinutes = elapsedSeconds / 60

        val res = when {
            elapsedSeconds < (JUST_NOW_MILLIS / 1000) -> "just now"
            elapsedSeconds <= 60 -> "$elapsedSeconds s"
            elapsedSeconds <= 60 * 60 -> "${elapsedMinutes}m"
            elapsedSeconds <= 60 * 60 * 24 -> "${elapsedSeconds / (60 * 60)}h"
            elapsedSeconds <= 60 * 60 * 24 * 7 -> "${elapsedSeconds / (60 * 60 * 24)}d"
            elapsedSeconds <= 60 * 60 * 24 * 30 -> "${elapsedSeconds / (60 * 60 * 24 * 7)}w"
            elapsedSeconds <= 60 * 60 * 24 * 365 -> "${elapsedSeconds / (60 * 60 * 24 * 30)}mo"
            else -> "${elapsedSeconds / (60 * 60 * 24 * 365)}y"
        }

        append(res)
    }
}

/*
 *  timestamp in millis
 */
@Composable
fun startDateFormatter(
    timestamp: Long,
): AnnotatedString {
    return buildAnnotatedString {
        val date = Date(timestamp)

        if (DateUtils.isToday(timestamp)) {
            append("Today")
            return@buildAnnotatedString
        }

        if (DateUtils.isToday(timestamp - DateUtils.DAY_IN_MILLIS)) {
            append("Tomorrow")
            return@buildAnnotatedString
        }

        if (Date().time > timestamp - 5 * DateUtils.DAY_IN_MILLIS) {
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
 *  timestamp in millis
 */
@Composable
fun timeFormatter(
    timestamp: Long,
): AnnotatedString {

    return buildAnnotatedString {
        val date = Date(timestamp)
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
    startTimestamp: Long,
    endTimestamp: Long = 0L,
): AnnotatedString {
    val now = System.currentTimeMillis() / 1000
    val isOngoing = (startTimestamp < now) && (endTimestamp > now)

    return buildAnnotatedString {
        val date = Date(startTimestamp * 1000)

        val time = SimpleDateFormat("HH:mm").format(date)

        if (isOngoing) {
            val timeLeft = relativeTimeFormatter(endTimestamp)
            append("Ongoing | $timeLeft left")
            return@buildAnnotatedString
        }

        if (DateUtils.isToday(startTimestamp * 1000)) {
            append("${stringResource(id = R.string.today)}, $time")
            return@buildAnnotatedString
        }

        if (DateUtils.isToday(startTimestamp * 1000 - DateUtils.DAY_IN_MILLIS)) {
            append("${stringResource(id = R.string.tomorrow)}, $time")
            return@buildAnnotatedString
        }

        if (Date().time > startTimestamp * 1000 - 5 * DateUtils.DAY_IN_MILLIS) {
            val res = SimpleDateFormat("EEEE, HH:mm", Locale.getDefault()).format(date)
            append(res)
            return@buildAnnotatedString
        }

        val res = SimpleDateFormat("EEE dd MMM, HH:mm", Locale.getDefault()).format(date)
        append(res)
        return@buildAnnotatedString
    }
}
