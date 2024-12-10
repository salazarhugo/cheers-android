package com.salazar.cheers.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent

@Composable
fun CreateNoteItem(
    picture: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val avatarModifier = Modifier
        .border(2.dp, Color.Transparent)
        .padding(4.dp)
    Bounce(
        onBounce = onClick,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.TopStart,
            ) {
                AvatarComponent(
                    avatar = picture,
                    modifier = avatarModifier,
                    size = 72.dp,
                    onClick = onClick,
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    contentDescription = null,
                )
            }
            Text(
                text = "Add a note",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
@ComponentPreviews
@Composable
private fun CreateNoteComponentPreview() {
    CheersPreview {
        CreateNoteItem(
            picture = null,
        )
    }
}
