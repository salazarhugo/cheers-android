package com.salazar.cheers.core.data.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun numberFormatter(
    value: Int,
): AnnotatedString {
    return buildAnnotatedString {
        val res = when {
            value < 10E3 -> value.toString()
            value < 10E6 -> String.format("%.1f", value / 10E2) + "K"
            else -> String.format("%.1f", value / 10E5) + "M"
        }
        append(res)
    }
}