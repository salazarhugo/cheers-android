package com.salazar.cheers.feature.friend_list

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent

@Composable
fun EmptyFriendListMessage(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    MessageScreenComponent(
        image = com.salazar.cheers.core.ui.R.drawable.ds_ic_error_screen,
        modifier = modifier.padding(16.dp),
        title = "No friends yet",
        subtitle = "Looks like you haven't found any party buddies",
    )
}

@ComponentPreviews
@Composable
private fun EmptyFeedPreview() {
    CheersPreview {
        EmptyFriendListMessage(
            modifier = Modifier,
        )
    }
}