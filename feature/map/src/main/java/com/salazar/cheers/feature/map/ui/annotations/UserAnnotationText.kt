package com.salazar.cheers.feature.map.ui.annotations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.GreenGoogle
import java.util.Date

@Composable
internal fun UserAnnotationText(
    name: String,
    modifier: Modifier = Modifier,
    lastUpdated: Long = Date().time / 1000,
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = MaterialTheme.typography.bodyMedium.toSpanStyle()) {
            append(name)
        }
        val timestamp =
            com.salazar.cheers.core.util.relativeTimeFormatter(seconds = lastUpdated).text

        withStyle(style = MaterialTheme.typography.labelSmall.toSpanStyle().copy(fontWeight = FontWeight.Bold)) {
            append(" ")
            if (((Date().time / 1000) - lastUpdated) < 2 ) {
                withStyle(style = SpanStyle(color = GreenGoogle)) {
                    append("now")
                }
            } else {
                append(timestamp)
            }
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier
            .offset(y = (-8).dp)
            .shadow(elevation = 9.dp, shape = RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(4.dp),
        color = Color.Black,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}

@ComponentPreviews
@Composable
private fun UserAnnotationTextPreview() {
    CheersPreview {
        UserAnnotationText(
            modifier = Modifier.padding(16.dp),
            name = "Me",
        )
        UserAnnotationText(
            modifier = Modifier.padding(16.dp),
            name = "Adrien",
        )
        UserAnnotationText(
            name = "Me",
            modifier = Modifier.padding(16.dp),
            lastUpdated = Date().time / 1000 - 60 * 6
        )
    }
}
