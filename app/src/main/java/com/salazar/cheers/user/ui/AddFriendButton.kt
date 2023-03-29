package com.salazar.cheers.user.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.compose.buttons.CheersOutlinedButton

@Composable
fun AddFriendButton(
    modifier: Modifier = Modifier,
    requestedByViewer: Boolean,
    onAddFriendClick: () -> Unit,
    onCancelFriendRequestClick: () -> Unit,
    onDelete: () -> Unit = {},
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    var requested by remember { mutableStateOf(requestedByViewer) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (requested)
            CheersOutlinedButton(
                onClick = {
                    requested = false
                    onCancelFriendRequestClick()
                },
                modifier = modifier,
                enabled = enabled,
                shape = shape,
            ) {
                Text("Requested")
            }
        else
            Button(
                modifier = modifier.height(34.dp),
                onClick = {
                    requested = true
                    onAddFriendClick()
                },
                shape = shape,
            ) {
                Text("Add")
            }

        if (!requested)
            IconButton(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                onClick = {
                    onDelete()
                    requested = false
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
    }
}

@Composable
@Preview
private fun FollowButtonPreview() {
    FollowButton(
        isFollowing = false,
        onClick = {}
    )
}

@Composable
@Preview
private fun FollowButtonOutlinedPreview() {
    FollowButton(
        isFollowing = true,
        onClick = {}
    )
}
