package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent

@Composable
fun EmptyFeed(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MessageScreenComponent(
        image = R.drawable.ic_cheers_logo,
        modifier = modifier.padding(16.dp),
        title = "Welcome to Cheers",
        subtitle = "Follow people to start seeing the photos and videos they share.",
        primaryButtonText = "Create post",
        onPrimaryButtonClick = onClick,
    )
}

@ComponentPreviews
@Composable
private fun EmptyFeedPreview() {
    CheersPreview {
        EmptyFeed(
            modifier = Modifier,
        )
    }
}
