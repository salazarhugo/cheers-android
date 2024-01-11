package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersOutlinedButton
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun AddFriendButton(
    requestedByViewer: Boolean,
    modifier: Modifier = Modifier,
    onAddFriendClick: () -> Unit = {},
    onCancelFriendRequestClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    var requested by remember { mutableStateOf(requestedByViewer) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (requested) {
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
        }
        else {
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
        }

        if (!requested) {
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
}

@Composable
@ComponentPreviews
private fun FollowButtonPreview() {
    CheersPreview {
        AddFriendButton(
            modifier = Modifier.padding(16.dp),
            requestedByViewer = false,
        )
    }
}

@Composable
@ComponentPreviews
private fun FollowButtonOutlinedPreview() {
    CheersPreview {
        AddFriendButton(
            modifier = Modifier.padding(16.dp),
            requestedByViewer = true,
        )
    }
}
