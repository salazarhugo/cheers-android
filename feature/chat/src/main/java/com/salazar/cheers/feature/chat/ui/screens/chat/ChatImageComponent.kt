package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.data.chat.models.mockMessage1


@Composable
fun ChatImageComponent(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onLongClickMessage: (String) -> Unit = {},
    onDoubleTapMessage: (String) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        message.images.forEach { image ->
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(4 / 5f)
                    .animateContentSize()
                    .padding(3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongClickMessage(message.id) },
                            onDoubleTap = { onDoubleTapMessage(message.id) },
                            onTap = {}
                        )
                    },
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun ChatImageComponentPreview(
) {
    CheersPreview {
        ChatImageComponent (
            message = mockMessage1.copy(images = listOf("")),
            modifier = Modifier.padding(16.dp),
        )
    }
}
