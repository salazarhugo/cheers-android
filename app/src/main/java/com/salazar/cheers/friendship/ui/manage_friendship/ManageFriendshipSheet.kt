package com.salazar.cheers.friendship.ui.manage_friendship

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ManageFriendshipSheet(
    modifier: Modifier = Modifier,
    onManageFriendshipUIAction: (ManageFriendshipUIAction) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.outline)
        )
        Item(
            text = "Block",
            icon = Icons.Outlined.Block,
            onClick = { onManageFriendshipUIAction(ManageFriendshipUIAction.OnBlockClick)},
            color = MaterialTheme.colorScheme.error,
        )
        Item(
            text = "Remove Friend",
            icon = Icons.Outlined.PersonRemove,
            onClick = { onManageFriendshipUIAction(ManageFriendshipUIAction.OnRemoveFriendClick)},
            color = MaterialTheme.colorScheme.error,
        )
        Item(
            text = "Edit name",
            icon = Icons.Outlined.Edit,
            onClick = { onManageFriendshipUIAction(ManageFriendshipUIAction.OnReportClick)},
        )
        Spacer(Modifier.height(12.dp))
//        Item(
//            icon = Icons.Outlined.Report,
//            onClick = { onManageFriendshipUIAction(ManageFriendshipUIAction.OnReportClick)},
//            color = MaterialTheme.colorScheme.error,
//        )
    }
}

@Composable
fun Item(
    text: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.onBackground,
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
            imageVector = icon,
            contentDescription = null,
            tint = color,
        )
        Spacer(Modifier.width(22.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = color,
        )
    }
}
