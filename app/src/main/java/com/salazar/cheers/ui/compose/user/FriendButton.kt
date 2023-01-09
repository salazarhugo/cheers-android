package com.salazar.cheers.ui.compose.user

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.compose.buttons.CheersOutlinedButton

@Composable
fun FriendButton(
    friend: Boolean,
    requested: Boolean,
    hasRequestedViewer: Boolean,
    onCancelFriendRequest: () -> Unit,
    onSendFriendRequest: () -> Unit,
    onAcceptFriendRequest: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    if (friend)
        return

    if (requested)
        CheersOutlinedButton(
            onClick = onCancelFriendRequest,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
        ) {
            Text("Cancel request")
        }
    else if (hasRequestedViewer) {
        Button(
            modifier = modifier.height(34.dp),
            shape = shape,
            onClick = onAcceptFriendRequest,
        ) {
            Text("Accept")
        }
        CheersOutlinedButton(
            onClick = onCancelFriendRequest,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
        ) {
            Text("Cancel")
        }
    }
    else
        Button(
            modifier = modifier.height(34.dp),
            onClick = onSendFriendRequest,
            shape = shape,
        ) {
            Text("Add friend")
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
