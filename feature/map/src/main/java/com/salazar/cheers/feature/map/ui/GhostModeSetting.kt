package com.salazar.cheers.feature.map.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.UserProfilePicture

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
        onClick = { onChange(!ghostMode) },
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
