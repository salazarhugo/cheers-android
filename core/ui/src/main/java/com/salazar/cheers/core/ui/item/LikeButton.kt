package com.salazar.cheers.core.ui.item

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun LikeButton(
    like: Boolean,
    onToggle: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val icon = if (like)
            Icons.Rounded.Favorite
        else
            Icons.Rounded.FavoriteBorder

        val transition = updateTransition(
            targetState = like, label = ""
        )

        val color by transition.animateColor(label = "Color") { state ->
            when (state) {
                true -> Color(0xFFF28E1C)
                false -> MaterialTheme.colorScheme.onBackground
            }
        }

        Bounce(onBounce = onToggle) {
            Icon(
                imageVector = icon,
                modifier = Modifier,
                contentDescription = null,
                tint = color
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun CommentButtonDarkPreview() {
    CheersPreview {
        LikeButton(
            like = false,
        )
        LikeButton(
            like = true,
        )
    }
}
