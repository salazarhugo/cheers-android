package com.salazar.cheers.feature.profile.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.Username

@Composable
fun ProfileTopBar(
    username: String,
    verified: Boolean,
    premium: Boolean,
    onBackPressed: () -> Unit,
    onMenuClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Username(
                username = username,
                premium = premium,
                verified = verified,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto
                ),
            )
        },
        actions = {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                )
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Outlined.Menu, null)
            }
        }
    )
}

