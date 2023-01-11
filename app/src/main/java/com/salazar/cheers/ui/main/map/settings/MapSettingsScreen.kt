package com.salazar.cheers.ui.main.map.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.compose.share.Toolbar

@Composable
fun MapSettingsScreen(
    uiState: MapSettingsUiState,
    onMapSettingsUIAction: (MapSettingsUIAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = { onMapSettingsUIAction(MapSettingsUIAction.OnBackPressed) },
                title = "Map settings",
            )
        },
    ) {
        MapSettingsList(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .padding(16.dp)
            ,
            onMapSettingsUIAction = onMapSettingsUIAction,
        )
    }
}

@Composable
fun MapSettingsList(
    modifier: Modifier = Modifier,
    onMapSettingsUIAction: (MapSettingsUIAction) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        item {
            Text(
                text = "My Location"
            )
        }

        item {
            GhostModeCard()
        }
    }
}

@Composable
fun GhostModeCard() {
    var ghostMode by remember { mutableStateOf(false) }
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Ghost Mode",
                style = MaterialTheme.typography.titleMedium,
            )
            Switch(
                checked = ghostMode,
                onCheckedChange = {
                    ghostMode = it
                },
                thumbContent = icon,
            )
        }
    }
}
