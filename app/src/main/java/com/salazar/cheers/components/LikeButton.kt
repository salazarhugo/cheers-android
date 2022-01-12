package com.salazar.cheers.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateSize
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.salazar.cheers.R

@Composable
fun LikeButton(
    like: Boolean,
    likes: Int,
    onToggle: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val icon = if (like)
            R.drawable.ic_glass_icon_full
        else
            R.drawable.ic_glass_icon

        val transition = updateTransition(
            targetState = like, label = ""
        )
        val color by transition.animateColor(label = "Color") { state ->
            when (state) {
                true -> Color(0xFFF28E1C)
                false -> MaterialTheme.colorScheme.onBackground
            }
        }

        Icon(
            rememberImagePainter(icon),
            null,
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    onToggle()
                },
            tint = color
        )
        Spacer(modifier = Modifier.width(6.dp))
        if (likes > 0)
            Text(
                text = likes.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
    }
}

@Composable
@Preview
private fun LikeButtonPreview() {
    LikeButton(like = true, likes = 643) { }
}

@Composable
@Preview
private fun UnLikeButtonOutlinedPreview() {
    LikeButton(like = false, likes = 643) { }
}
