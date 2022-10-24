package com.salazar.cheers.compose.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.main.profile.SheetItem

sealed class StorySheetUIAction {
    object OnNfcClick : StorySheetUIAction()
    object OnCopyStoryClick : StorySheetUIAction()
    object OnSettingsClick : StorySheetUIAction()
    object OnDeleteClick : StorySheetUIAction()
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
        SheetItem(
            text = "Delete",
            icon = Icons.Outlined.Delete,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnDeleteClick) },
        )
        SheetItem(
            text = "Save video",
            icon = Icons.Outlined.Archive,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnAddSnapchatFriends) }
        )
        SheetItem(
            text = "Send to...",
            icon = Icons.Outlined.Archive,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnPostHistoryClick) }
        )
        SheetItem(
            text = "Share as post...",
            icon = Icons.Outlined.Contactless,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnNfcClick) }
        )
        SheetItem(text = "Copy link", icon = Icons.Outlined.QrCode)
        SheetItem(text = "Saved", icon = Icons.Outlined.BookmarkBorder)
        SheetItem(
            text = "Share to...",
            icon = Icons.Outlined.ContentCopy,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnCopyStoryClick) }
        )
        SheetItem(
            text = "Story settings",
            icon = Icons.Outlined.ContentCopy,
            onClick = { onStorySheetUIAction(StorySheetUIAction.OnCopyStoryClick) }
        )
    }
}