package com.salazar.cheers.ui.sheets.manage_friendship

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
            text = "Report",
            icon = Icons.Outlined.Report,
            onClick = { onManageFriendshipUIAction(ManageFriendshipUIAction.OnReportClick)},
        )
        Item(
            text = "Block",
            icon = Icons.Outlined.Block,
            onClick = { onManageFriendshipUIAction(ManageFriendshipUIAction.OnBlockClick)},
        )
        Item(
            text = "Remove Friend",
            icon = Icons.Outlined.UnfoldLess,
            onClick = { onManageFriendshipUIAction(ManageFriendshipUIAction.OnRemoveFriendClick)},
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
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.width(22.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
