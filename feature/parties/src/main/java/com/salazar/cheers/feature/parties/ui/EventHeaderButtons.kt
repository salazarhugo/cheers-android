package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.WatchStatus

@Composable
fun PartyHeaderButtons(
    isHost: Boolean,
    watchStatus: WatchStatus,
    onManageClick: () -> Unit,
    onInviteClick: () -> Unit,
    onWatchStatusChange: (WatchStatus) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (isHost) {
            Button(
                onClick = onManageClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Handyman, null)
                Spacer(Modifier.width(4.dp))
                Text("Manage")
                Icon(Icons.Default.ArrowDropDown, null)
            }
            Spacer(Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onInviteClick,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f),
            ) {
                Icon(Icons.Default.Email, null)
                Spacer(Modifier.width(4.dp))
                Text("Invite")
            }
        } else {
            var expanded by remember { mutableStateOf(false) }

            PartyWatchStatusButton(
                watchStatus = watchStatus,
                onGoingToggle = {
                    expanded = true
                },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                DropdownMenuItem(
                    text = { Text("Interested") },
                    onClick = { onWatchStatusChange(WatchStatus.INTERESTED) },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        RadioButton(
                            selected = watchStatus == WatchStatus.INTERESTED,
                            onClick = { onWatchStatusChange(WatchStatus.INTERESTED) }
                        )
                    },
                )
                DropdownMenuItem(
                    text = { Text("Going") },
                    onClick = { onWatchStatusChange(WatchStatus.GOING) },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        RadioButton(
                            selected = watchStatus == WatchStatus.GOING,
                            onClick = { onWatchStatusChange(WatchStatus.GOING) }
                        )
                    },
                )
                DropdownMenuItem(
                    text = { Text("Not Going") },
                    onClick = { onWatchStatusChange(WatchStatus.UNWATCHED) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        RadioButton(
                            selected = watchStatus == WatchStatus.UNWATCHED,
                            onClick = { onWatchStatusChange(WatchStatus.UNWATCHED) }
                        )
                    },
                )
            }
        }
    }
}
