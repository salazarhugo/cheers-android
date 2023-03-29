package com.salazar.cheers.internal

import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.salazar.cheers.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/*
 *  epoch in seconds
 */
@Composable
fun relativeTimeFormatter(
    epoch: Long,
): AnnotatedString {

    return buildAnnotatedString {

        val today = Date()
        var diff: Long = today.time / 1000 - epoch

        if (diff < 0)
            diff *= -1

        val seconds = diff
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        val res = when {
            days > 0 -> TimeUnit.SECONDS.toDays(diff).toString() + 'd'
            hours > 0 -> TimeUnit.SECONDS.toHours(diff).toString() + 'h'
            minutes >= 1 -> TimeUnit.SECONDS.toMinutes(diff).toString() + 'm'
            else -> "just now"
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
