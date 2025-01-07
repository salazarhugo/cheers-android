package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.data.chat.models.mockMessage1


@Composable
fun ReplyChatMessage(
    message: ChatMessage,
    shape: Shape = RoundedCornerShape(10.dp),
    containerColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable { onClick() },
        color = containerColor.copy(alpha = 0.3f),
        shape = shape,
        contentColor = containerColor,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                thickness = 4.dp,
                color = containerColor
            )
            Column {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = message.senderName.ifBlank { message.senderUsername },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

            }
        }
    }
}

@ComponentPreviews
@Composable
private fun ReplyChatMessagePreview(
    @PreviewParameter(LoremIpsum::class) text: String
) {
    CheersPreview {
        ReplyChatMessage(
            modifier = Modifier.padding(16.dp),
            message = mockMessage1.copy(text = text, senderName = "Hugo"),
        )
    }
}
