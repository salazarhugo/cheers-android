package com.salazar.cheers.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Composable
fun relativeTimeFormatter(
    value: String,
): AnnotatedString {

    return buildAnnotatedString {
        if (value.isBlank())
            return@buildAnnotatedString

        val a = Duration.parseOrNull(java.time.Duration.between(ZonedDateTime.parse(value), ZonedDateTime.now()).toString())
            ?: return@buildAnnotatedString

        val res = when {
            a.inWholeDays > 0 -> a.toString(DurationUnit.DAYS)
            a.inWholeHours > 0 -> a.toString(DurationUnit.HOURS)
            a.inWholeMinutes > 0 -> a.toString(DurationUnit.MINUTES)
            a.inWholeSeconds > 0 -> a.toString(DurationUnit.SECONDS)
            else -> ""
        }

        append(res)
    }
}
