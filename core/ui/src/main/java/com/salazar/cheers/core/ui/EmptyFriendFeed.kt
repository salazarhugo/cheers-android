package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.login_message.LoginMessageScreen
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent

@Composable
fun EmptyFriendFeed(
    modifier: Modifier = Modifier,
    isSignedIn: Boolean,
    onClick: () -> Unit = {},
) {
    if (isSignedIn) {
        MessageScreenComponent(
            image = R.drawable.ic_cheers_logo,
            modifier = modifier.padding(16.dp),
            title = "Welcome to Cheers",
            subtitle = "Follow people to start seeing the photos and videos they share.",
            primaryButtonText = "Create post",
            onPrimaryButtonClick = onClick,
        )
    } else {
        LoginMessageScreen(
            onSignInClick = onClick,
            onRegisterClick = onClick,
        )
    }
}

@ComponentPreviews
@Composable
private fun EmptyFriendFeedPreview() {
    CheersPreview {
        EmptyFriendFeed(
            isSignedIn = true,
            modifier = Modifier,
        )
    }
}

@ComponentPreviews
@Composable
private fun EmptyFriendFeedPreviewNotSignedIn() {
    CheersPreview {
        EmptyFriendFeed(
            isSignedIn = false,
            modifier = Modifier,
        )
    }
}
