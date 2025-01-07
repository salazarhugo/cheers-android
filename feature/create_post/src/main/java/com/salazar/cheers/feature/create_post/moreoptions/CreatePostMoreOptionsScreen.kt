package com.salazar.cheers.feature.create_post.moreoptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material.icons.outlined.MotionPhotosOff
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.feature.create_post.CreatePostUIAction
import com.salazar.cheers.feature.create_post.SwitchPreference

@Composable
fun CreatePostMoreOptionsScreen(
    modifier: Modifier = Modifier,
    notificationEnabled: Boolean,
    likesEnabled: Boolean,
    commentsEnabled: Boolean,
    shareEnabled: Boolean,
    onCreatePostMoreOptionsUIAction: (CreatePostUIAction) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                title = "More options",
                onBackPressed = {
                    onCreatePostMoreOptionsUIAction(CreatePostUIAction.OnBackPressed)
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SwitchPreference(
                text = "Turn off notifications",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsOff,
                        contentDescription = "Notifications off"
                    )
                },
                checked = !notificationEnabled,
                onCheckedChange = {
                    onCreatePostMoreOptionsUIAction(CreatePostUIAction.OnNotificationChange(!it))
                },
            )
            HorizontalDivider(
                thickness = 0.5.dp,
            )
            SwitchPreference(
                text = "Turn off liking",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.HeartBroken,
                        contentDescription = "Likes off"
                    )
                },
                checked = !likesEnabled,
                onCheckedChange = {
                    onCreatePostMoreOptionsUIAction(CreatePostUIAction.OnEnableLikesChange(it.not()))
                },
            )
            SwitchPreference(
                text = "Turn off commenting",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.MotionPhotosOff,
                        contentDescription = "Comments off",
                    )
                },
                checked = !commentsEnabled,
                onCheckedChange = {
                    onCreatePostMoreOptionsUIAction(CreatePostUIAction.OnEnableCommentsChange(it.not()))
                },
            )
            SwitchPreference(
                text = "Turn off sharing",
                checked = !shareEnabled,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = "Sharing off",
                    )
                },
                onCheckedChange = {
                    onCreatePostMoreOptionsUIAction(CreatePostUIAction.OnEnableShareChange(it.not()))
                },
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun CreatePostMoreOptionsScreenPreview() {
    CheersPreview {
        CreatePostMoreOptionsScreen(
            modifier = Modifier,
            commentsEnabled = true,
            shareEnabled = true,
            likesEnabled = true,
            notificationEnabled = true,
        )
    }
}