package com.salazar.cheers.feature.chat.ui.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.theme.GreySheet
import com.salazar.cheers.data.chat.models.ChatChannel
import com.salazar.cheers.data.chat.models.ChatType

@Composable
internal fun ChatsMoreBottomSheet(
    chat: ChatChannel,
    modifier: Modifier = Modifier,
    viewModel: ChatsSheetViewModel = hiltViewModel(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets(0, WindowInsets.statusBars.getTop(LocalDensity.current), 0, 0),
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = chat.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onBackground,
            )
            HorizontalDivider()
            if (chat.type == ChatType.GROUP)
                Item(
                    text = "Leave",
                    icon = Icons.AutoMirrored.Outlined.ExitToApp,
                    red = true,
                    onClick = {
                        viewModel.leaveChannel(chat.id) {
                            onDismissRequest()
                        }
                    },
                )
            if (chat.admin)
                Item(
                    text = "Delete Group",
                    icon = Icons.Outlined.Delete,
                    red = true,
                    onClick = {
                        viewModel.deleteChannel(chat.id) {
                            onDismissRequest()
                        }
                    },
                )
            Item(
                text = "Delete Chats",
                icon = Icons.Outlined.Delete,
                red = false,
                onClick = {
                    viewModel.deleteChats(chat.id) {
                        onDismissRequest()
                    }
                },
            )
            Item(text = "Mute messages", icon = Icons.Outlined.NotificationsOff)
            Item(text = "Mute call notifications", icon = Icons.Outlined.NotificationsOff)
        }
    }
}

@Composable
private fun Item(
    text: String,
    icon: ImageVector,
    red: Boolean = false,
    onClick: () -> Unit = {}
) {
    val color = if (red) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurface

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
            tint = color,
        )
        Spacer(Modifier.width(22.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = color,
        )
    }
}

@Preview
@Composable
private fun ChatsMorePreview() {
    CheersPreview {
        ChatsMoreBottomSheet(
            chat = ChatChannel(),
            modifier = Modifier,
        )
    }
}