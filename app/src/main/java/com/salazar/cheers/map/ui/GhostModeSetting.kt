package com.salazar.cheers.map.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.compose.share.UserProfilePicture

@Composable
fun GhostModeCard(
    ghostMode: Boolean,
    picture: String?,
    onChange: (Boolean) -> Unit,
) {
    val icon: (@Composable () -> Unit)? = if (ghostMode) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else {
        null
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { /*TODO*/ },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UserProfilePicture(
                    picture = picture,
                )
                Column() {
                    Text(
                        text = "Ghost Mode",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "When this is enabled, your friends can't see your location.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Switch(
                checked = ghostMode,
                onCheckedChange = onChange,
                thumbContent = icon,
            )
        }
    }
}
