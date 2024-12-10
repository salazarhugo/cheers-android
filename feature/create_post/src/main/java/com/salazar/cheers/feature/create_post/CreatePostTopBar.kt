package com.salazar.cheers.feature.create_post

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.salazar.cheers.core.ui.theme.Roboto

@Composable
fun CreatePostTopBar(
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    isLoading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = "New post",
                fontWeight = FontWeight.Bold,
                fontFamily = Roboto,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onDismiss,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon",
                )
            }
        },
        actions = {}
    )
}
