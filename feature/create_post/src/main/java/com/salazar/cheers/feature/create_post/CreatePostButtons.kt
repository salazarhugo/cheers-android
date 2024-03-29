package com.salazar.cheers.feature.create_post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.SportsBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun CreatePostButtons(
    modifier: Modifier = Modifier,
    onAddDrinkClick: () -> Unit = {},
    onMicrophoneClick: () -> Unit = {},
    onAddImageClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Bounce(onBounce = onAddDrinkClick) {
            Icon(
                imageVector = Icons.Outlined.SportsBar,
                contentDescription = null,
            )
        }
        Bounce(onBounce = onAddImageClick) {
            Icon(
                imageVector = Icons.Outlined.AddPhotoAlternate,
                contentDescription = null,
            )
        }
        Bounce(onBounce = onMicrophoneClick) {
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = null,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun CreatePostButtonsPreview() {
    CheersPreview {
        CreatePostButtons(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
    }
}
