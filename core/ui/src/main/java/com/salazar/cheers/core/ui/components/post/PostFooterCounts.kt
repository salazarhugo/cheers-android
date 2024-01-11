package com.salazar.cheers.core.ui.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.animations.AnimatedTextCounter
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.common.ui.extensions.noRippleClickable

@Composable
fun PostFooterCounters(
    likeCount: Int,
    commentCount: Int,
    modifier: Modifier = Modifier,
    onLikesClick: () -> Unit = {},
    onCommentsClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (commentCount > 0) {
            Row(
                modifier = Modifier.noRippleClickable { onCommentsClick() },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AnimatedTextCounter(
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
                AnimatedTextCounter(
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
