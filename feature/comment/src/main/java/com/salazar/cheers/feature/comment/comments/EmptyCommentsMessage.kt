package com.salazar.cheers.feature.comment.comments

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent

@Composable
fun EmptyCommentsMessage(
    modifier: Modifier = Modifier,
) {
    MessageScreenComponent(
        modifier = modifier.padding(16.dp),
        title = "No comments yet",
        subtitle = "Start the conversation",
    )
}

@ComponentPreviews
@Composable
private fun EmptyCommentsPreview() {
    CheersPreview {
        EmptyCommentsMessage(
            modifier = Modifier,
        )
    }
}
