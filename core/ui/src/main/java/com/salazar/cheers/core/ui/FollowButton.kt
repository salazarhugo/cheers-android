package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FriendButton(
    isFriend: Boolean,
    modifier: Modifier = Modifier,
    requested: Boolean = false,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    if (isFriend) {
        CheersOutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
        ) {
            Text("Friend")
        }
    }
    else if (requested) {
        CheersOutlinedButton(
            onClick = onClick,
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
            onClick = onClick,
            shape = shape,
        ) {
            Text("Add friend")
        }
    }
}

@Composable
@Preview
private fun FriendButtonPreview() {
    CheersPreview {
        FriendButton(
            modifier = Modifier.padding(16.dp),
            isFriend = false,
            requested = false,
        )
        FriendButton(
            modifier = Modifier.padding(16.dp),
            isFriend = true,
            requested = false,
        )
        FriendButton(
            modifier = Modifier.padding(16.dp),
            isFriend = false,
            requested = true,
        )
    }
}