package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.modifier.clickableNullable


@Composable
fun Username(
    modifier: Modifier = Modifier,
    username: String,
    premium: Boolean = false,
    verified: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    maxLines: Int = 1,
    onClick: (() -> Unit)? = null,
    trailingText: AnnotatedString? = null,
) {
    val verifiedId = "verifiedIcon"
    val premiumId = "premiumIcon"
    val inlineContent = mapOf(
        Pair(
            verifiedId,
            InlineTextContent(
                Placeholder(
                    width = 14.sp,
                    height = 14.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                VerifiedComponent(
                    modifier = Modifier.fillMaxSize(),
                    textStyle = textStyle,
                )
            }
        ),
        Pair(
            premiumId,
            InlineTextContent(
                Placeholder(
                    width = 14.sp,
                    height = 14.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                PremiumComponent(
                    modifier = Modifier.fillMaxSize(),
                    textStyle = textStyle,
                )
            }
        )
    )
    val text = buildAnnotatedString {
        append(username)
        if (verified) {
            append(" ")
            appendInlineContent(verifiedId, "[verified]")
        }
        if (premium) {
            append(" ")
            appendInlineContent(premiumId, "[premium]")
        }
        if (trailingText != null) {
            append(" ")
            append(trailingText)
        }
    }
    Row(
        modifier = modifier.clickableNullable(onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            inlineContent = inlineContent,
            style = textStyle,
            color = color,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines,
        )
    }
}

@ComponentPreviews
@Composable
fun UsernamePreview() {
    CheersPreview {
        Username(
            username = "hugolsalazar",
            verified = true,
            premium = true,
            trailingText = AnnotatedString("is drinking beer"),
        )
    }
}
