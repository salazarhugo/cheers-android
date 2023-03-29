package com.salazar.cheers.user.ui

import androidx.compose.foundation.layout.height
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
fun FollowButton(
    isFollowing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    if (isFollowing)
        CheersOutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
        ) {
            Text("Following")
        }
    else
        Button(
            modifier = modifier.height(34.dp),
            onClick = onClick,
            shape = shape,
        ) {
            Text("Follow")
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
