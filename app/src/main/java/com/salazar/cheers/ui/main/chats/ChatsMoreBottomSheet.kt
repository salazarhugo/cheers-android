package com.salazar.cheers.ui.main.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.RoomType
import com.salazar.cheers.compose.DividerM3

@Composable
fun ChatsMoreBottomSheet(
    name: String,
    ownerId: String,
    roomType: RoomType,
    onDeleteClick: () -> Unit,
    onDeleteChats: () -> Unit,
    onLeaveClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )
        DividerM3()
        if (roomType == RoomType.GROUP) {
            Item(
                text = "Leave",
                icon = Icons.Outlined.ExitToApp,
                red = true,
                onClick = onLeaveClick,
            )
            val UUID by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid!!) }

            if (UUID == ownerId)
                Item(
                    text = "Delete Group",
                    icon = Icons.Outlined.Delete,
                    red = true,
                    onClick = onDeleteClick,
                )
        }
        Item(
            text = "Delete Chats",
            icon = Icons.Outlined.Delete,
            red = false,
            onClick = onDeleteChats,
        )
        Item(text = "Mute messages", icon = Icons.Outlined.NotificationsOff)
        Item(text = "Mute call notifications", icon = Icons.Outlined.NotificationsOff)
    }
}

@Composable
fun Item(
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
