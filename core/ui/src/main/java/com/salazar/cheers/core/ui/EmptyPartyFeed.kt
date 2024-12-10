package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent

@Composable
fun EmptyPartyFeed(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MessageScreenComponent(
        modifier = modifier.padding(16.dp),
        title = "No parties found",
        primaryButtonText = "Change city",
        onPrimaryButtonClick = onClick,
    )
}

@ComponentPreviews
@Composable
private fun EmptyFeedPreview() {
    CheersPreview {
        EmptyPartyFeed(
            modifier = Modifier,
        )
    }
}