package com.salazar.cheers.components.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.buttons.CheersOutlinedButton

@Composable
fun FollowButton(
    isFollowing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
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
