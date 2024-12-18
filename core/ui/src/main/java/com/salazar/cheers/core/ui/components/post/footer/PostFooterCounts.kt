package com.salazar.cheers.core.ui.components.post.footer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.animations.AnimatedIntCounter
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.extensions.noRippleClickable

@Composable
fun PostFooterCounters(
    likeCount: Int,
    commentCount: Int,
    modifier: Modifier = Modifier,
    onLikesClick: () -> Unit = {},
    onCommentsClick: () -> Unit = {},
) {
    if (commentCount <= 0 && likeCount <= 0) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (commentCount > 0) {
            Row(
                modifier = Modifier.noRippleClickable { onCommentsClick() },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AnimatedIntCounter(
                    targetState = commentCount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = pluralStringResource(id = R.plurals.comments, count = commentCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        if (likeCount > 0) {
            Row(
                modifier = Modifier.noRippleClickable { onLikesClick() },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AnimatedIntCounter(
                    targetState = likeCount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = pluralStringResource(id = R.plurals.likes, count = likeCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun PostFooterCountersPreview() {
    CheersPreview {
        PostFooterCounters(
            likeCount = 42623,
            commentCount = 42623,
            modifier = Modifier,
        )
    }
}
