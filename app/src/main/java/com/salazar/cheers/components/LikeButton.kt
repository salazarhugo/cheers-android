package com.salazar.cheers.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.components.animations.AnimatedTextCounter

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
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onToggle()
                },
            tint = color
        )
        if (likes > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            AnimatedTextCounter(
                targetState = likes,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun LikeButtonPreview() {
    LikeButton(like = true, likes = 643) { }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
private fun UnLikeButtonOutlinedPreview() {
    LikeButton(like = false, likes = 643) { }
}
