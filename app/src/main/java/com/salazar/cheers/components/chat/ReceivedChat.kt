package com.salazar.cheers.components.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.salazar.cheers.MessageType
import com.salazar.cheers.components.CircularProgressIndicatorM3
import com.salazar.cheers.ui.theme.BlueCheers

@Preview
@Composable
fun OpenedChat() {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawLine(
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height / 2),
                color = BlueCheers,
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
            drawLine(
                start = Offset(size.width, size.height / 2),
                end = Offset(0f, size.height),
                color = BlueCheers,
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
            val path = Path().apply {
                moveTo(0f, 0f)
                quadraticBezierTo(
                    size.width * 0.4f,
                    size.height / 2,
                    0f,
                    size.height,
                )
            }
            drawPath(
                path = path,
                color = BlueCheers,
                style = Stroke(width = 4f, cap = StrokeCap.Round),
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Opened",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Preview
@Composable
fun DeliveredChat(
    @PreviewParameter(MessageTypeProvider::class)
    messageType: MessageType
) {
    val color = messageType.toColor()

    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(10.dp)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                quadraticBezierTo(
                    size.width * 0.4f,
                    size.height / 2,
                    0f,
                    size.height,
                )
                lineTo(size.width, size.height / 2)
                moveTo(size.width, size.height / 2)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = path,
                color = color,
                style = Fill,
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Delivered",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Preview
@Composable
fun ReceivedChat(
    @PreviewParameter(MessageTypeProvider::class)
    messageType: MessageType
) {
    val color = messageType.toColor()

    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 4f)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Received",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Preview
@Composable
fun SendingChat() {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicatorM3(
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Sending",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
    }
}

fun MessageType.toColor(): Color {
    return when (this) {
        MessageType.TEXT -> BlueCheers
        MessageType.IMAGE -> Color(0xFFF23C57)
        MessageType.VIDEO -> Color(0xFF7F00FF)
        else -> BlueCheers
    }
}

@Composable
fun NewChat(messageType: MessageType) {

    val text = when (messageType) {
        MessageType.TEXT -> "New Chat"
        MessageType.IMAGE -> "New Flash"
        MessageType.VIDEO -> "New Video"
        else -> ""
    }

    val color = messageType.toColor()

    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(8f, 8f),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Preview
@Composable
fun EmptyChat() {
    val color = MaterialTheme.colorScheme.onBackground
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawRoundRect(
                color = color,
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 4f)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Say hi!",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        )
    }
}

class MessageTypeProvider : PreviewParameterProvider<MessageType> {
    override val values = sequenceOf(
        MessageType.TEXT,
        MessageType.IMAGE,
        MessageType.VIDEO
    )
}