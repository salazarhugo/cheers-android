package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.animations.Bounce
import com.salazar.cheers.core.ui.item.CommentButton
import com.salazar.cheers.core.ui.item.LikeButton

@Composable
fun PostFooterButtons(
    hasViewerLiked: Boolean,
    onLike: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LikeButton(
            like = hasViewerLiked,
            onToggle = onLike,
        )
        CommentButton(
            onClick = onCommentClick,
        )
        Bounce(onBounce = onShareClick) {
            Icon(
                modifier = Modifier.size(24.dp).padding(2.dp),
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

