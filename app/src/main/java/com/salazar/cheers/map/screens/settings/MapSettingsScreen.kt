package com.salazar.cheers.map.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.map.ui.GhostModeCard
import com.salazar.cheers.ui.compose.share.Toolbar

@Composable
fun MapSettingsScreen(
    uiState: MapSettingsUiState,
    onMapSettingsUIAction: (MapSettingsUIAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            Toolbar(
                onBackPressed = { onMapSettingsUIAction(MapSettingsUIAction.OnBackPressed) },
                title = "Map settings",
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(top = it.calculateTopPadding()),
        ) {
            MapSettingsList(
                uiState = uiState,
                onMapSettingsUIAction = onMapSettingsUIAction,
            )
        }
    }
}

@Composable
fun MapSettingsList(
    uiState: MapSettingsUiState,
    onMapSettingsUIAction: (MapSettingsUIAction) -> Unit,
) {
    val picture = uiState.user?.picture
    val ghostMode = uiState.settings?.ghostMode ?: false

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
    ) {
        item {
            GhostModeCard(
                ghostMode = ghostMode,
                picture = picture,
                onChange = {
                    onMapSettingsUIAction(MapSettingsUIAction.OnGhostModeChange(it))
                },
            )
        }
    }
}