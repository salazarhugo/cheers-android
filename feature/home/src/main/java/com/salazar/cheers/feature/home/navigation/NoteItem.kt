package com.salazar.cheers.feature.home.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.modifier.animatedBorder
import com.salazar.cheers.data.note.Note
import com.salazar.cheers.data.note.NoteType
import com.salazar.common.ui.extensions.noRippleClickable

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    val avatarModifier = when(note.type) {
        NoteType.DRINKING ->
            Modifier
                .animatedBorder(
                    initialColor = Color(0xFF45CA6C),
                    targetColor = Color(0xFF0AB7AE),
                    shape = CircleShape,
                    borderWidth = 2.dp
                )
                .padding(4.dp)
        NoteType.SEARCHING ->
            Modifier
                .animatedBorder(
                    initialColor = Color(0xFFFFA500),
                    targetColor = Color(0xFFFF5F1F),
                    shape = CircleShape,
                    borderWidth = 2.dp,
                )
                .padding(4.dp)
        else -> Modifier.padding(6.dp)
    }

    Bounce(
        modifier = Modifier.noRippleClickable { onClick(note.userId) },
        onBounce = { onClick(note.userId) },
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
            ) {
                AvatarComponent(
                    modifier = avatarModifier,
                    avatar = note.picture,
                    size = 72.dp,
                )
                if (note.type == NoteType.DRINKING) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(note.drinkIcon)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(46.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                        ,
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .sizeIn(maxWidth = 72.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp),
                        text = note.text,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                    )
                }
            }
            Text(
                text = note.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun NoteComponentPreview() {
    CheersPreview {
        NoteItem(
            note = Note(
                username = "cheers",
                name = "Cheers",
                userId = "",
            ),
        )
    }
}