package com.salazar.cheers.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.salazar.cheers.core.model.Note
import com.salazar.cheers.core.model.NoteType
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.extensions.noRippleClickable
import com.salazar.cheers.core.ui.modifier.animatedBorder

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    onUserClick: (String) -> Unit = {},
) {
    val avatarSize = 33.dp
    val avatarHalf = avatarSize.div(2.dp)
    val avatarModifier = when (note.type) {
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

        else -> Modifier
            .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
    }

    Bounce(
        modifier = Modifier.noRippleClickable { onClick(note.userId) },
        onBounce = { onClick(note.userId) },
    ) {
        Box(
            modifier = modifier.padding(start = 0.dp, top = avatarHalf.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
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
                        .padding(8.dp),
                )
            } else {
                Text(
                    modifier = Modifier
                        .sizeIn(maxHeight = avatarSize * 2, maxWidth = avatarSize * 4)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = avatarHalf.dp, vertical = avatarHalf.dp - 4.dp),
                    text = note.text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                )
            }
            AvatarComponent(
                avatar = note.picture,
                username = note.username,
                name = note.name,
                modifier = Modifier
                    .offset(x = 0.dp, y = -(avatarHalf).dp)
                    .align(Alignment.TopStart)
                    .then(avatarModifier),
                size = avatarSize,
                onClick = { onUserClick(note.userId) },
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun NoteComponentPreview() {
    CheersPreview {
        NoteItem(
//            modifier = Modifier.padding(16.dp),
            note = Note(
                username = "cheers",
                name = "Cheers",
                userId = "",
                text = "Wanna hang out?"
            ),
        )
    }
}