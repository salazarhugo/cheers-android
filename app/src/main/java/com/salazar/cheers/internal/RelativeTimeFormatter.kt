package com.salazar.cheers.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun relativeTimeFormatter(
    timestamp: Long,
): AnnotatedString {

    return buildAnnotatedString {

        val today = Date()
        val diff: Long = today.time - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        val res = when {
            days > 0 -> TimeUnit.MILLISECONDS.toDays(diff).toString() + 'd'
            hours > 0 -> TimeUnit.MILLISECONDS.toHours(diff).toString() + 'h'
            minutes > 0 ->  TimeUnit.MILLISECONDS.toMinutes(diff).toString() + 'm'
            seconds > 0 ->  TimeUnit.MILLISECONDS.toSeconds(diff).toString() +  's'
            else -> ""
        }

        append(res)
    }
}
