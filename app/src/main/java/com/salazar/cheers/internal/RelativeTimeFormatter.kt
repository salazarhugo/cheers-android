package com.salazar.cheers.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import java.util.*
import java.util.concurrent.TimeUnit

/*
 *  timestamp in seconds
 */
@Composable
fun relativeTimeFormatter(
    timestamp: Long,
): AnnotatedString {

    return buildAnnotatedString {

        val today = Date()
        val diff: Long = today.time / 1000 - timestamp
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
