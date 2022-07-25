package com.salazar.cheers.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class StorySheetUIAction {
    object OnNfcClick : StorySheetUIAction()
    object OnCopyStoryClick : StorySheetUIAction()
    object OnSettingsClick : StorySheetUIAction()
    object OnAddSnapchatFriends : StorySheetUIAction()
    object OnPostHistoryClick : StorySheetUIAction()
}

@Composable
fun StoryMoreBottomSheet(
    onStorySheetUIAction: (StorySheetUIAction) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline)
        )
        Item(
            text = "Delete",
            icon = Icons.Outlined.Delete,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnSettingsClick) }
        )
        Item(
            text = "Save video",
            icon = Icons.Outlined.Archive,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnAddSnapchatFriends) }
        )
        Item(
            text = "Send to...",
            icon = Icons.Outlined.Archive,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnPostHistoryClick) }
        )
        Item(
            text = "Share as post...",
            icon = Icons.Outlined.Contactless,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnNfcClick) }
        )
        Item(text = "Copy link", icon = Icons.Outlined.QrCode)
        Item(text = "Saved", icon = Icons.Outlined.BookmarkBorder)
        Item(
            text = "Share to...",
            icon = Icons.Outlined.ContentCopy,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnCopyStoryClick) }
        )
        Item(
            text = "Story settings",
            icon = Icons.Outlined.ContentCopy,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnCopyStoryClick) }
        )
    }
}

@Composable
fun Item(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.width(22.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
