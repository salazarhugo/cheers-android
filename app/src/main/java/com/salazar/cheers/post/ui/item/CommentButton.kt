package com.salazar.cheers.post.ui.item

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.animations.AnimatedTextCounter
import com.salazar.cheers.core.ui.animations.Bounce

@Composable
fun CommentButton(
    comments: Int,
    onClick: () -> Unit,
) {
    Bounce(onBounce = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon),
                contentDescription = null
            )
            if (comments > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                AnimatedTextCounter(
                    targetState = comments,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
private fun CommentButtonDarkPreview() {
    CommentButton(comments = 643) { }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
private fun CommentButtonPreview() {
    CommentButton(comments = 643) { }
}
