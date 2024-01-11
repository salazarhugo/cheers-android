package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun RequestFriendButton(
    requested: Boolean,
    hasRequestedViewer: Boolean,
    modifier: Modifier = Modifier,
    onCancelFriendRequest: () -> Unit = {},
    onSendFriendRequest: () -> Unit = {},
    onAcceptFriendRequest: () -> Unit = {},
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (hasRequestedViewer) {
            AcceptOrDeclineButtons(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                onAcceptFriendRequest = onAcceptFriendRequest,
                onCancelFriendRequest = onCancelFriendRequest
            )
        }
        if (requested)
            CheersOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCancelFriendRequest,
                enabled = enabled,
                shape = shape,
            ) {
                Text("Cancel request")
            }
        else if (!hasRequestedViewer)
            Button(
                modifier = Modifier.height(34.dp).fillMaxWidth(),
                onClick = onSendFriendRequest,
                shape = shape,
            ) {
                Text("Add friend")
            }
    }
}

@Composable
private fun AcceptOrDeclineButtons(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape,
    onAcceptFriendRequest: () -> Unit,
    onCancelFriendRequest: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            modifier = Modifier.height(34.dp).weight(1f),
            shape = shape,
            onClick = onAcceptFriendRequest,
        ) {
            Text("Accept")
        }
        CheersOutlinedButton(
            modifier = Modifier.height(34.dp).weight(1f),
            onClick = onCancelFriendRequest,
            enabled = enabled,
            shape = shape,
        ) {
            Text("Delete")
        }
    }
}

@Composable
@ComponentPreviews
private fun FollowButtonPreviewDefault() {
    CheersPreview(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RequestFriendButton(
            requested = false,
            hasRequestedViewer = false,
        )
    }
}

@Composable
@ComponentPreviews
private fun FollowButtonPreviewRequested() {
    CheersPreview(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RequestFriendButton(
            requested = true,
            hasRequestedViewer = false,
        )
    }
}

@Composable
@ComponentPreviews
private fun FollowButtonPreviewHasRequestedViewer() {
    CheersPreview(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RequestFriendButton(
            requested = false,
            hasRequestedViewer = true,
        )
    }
}
