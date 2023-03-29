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
        topBar = {
            Toolbar(
                onBackPressed = { onMapSettingsUIAction(MapSettingsUIAction.OnBackPressed) },
                title = "Map settings",
            )
        },
    ) {
        MapSettingsList(
            uiState = uiState,
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .padding(16.dp),
            onMapSettingsUIAction = onMapSettingsUIAction,
        )
    }
}

@Composable
fun MapSettingsList(
    uiState: MapSettingsUiState,
    modifier: Modifier = Modifier,
    onMapSettingsUIAction: (MapSettingsUIAction) -> Unit,
) {
    val picture = uiState.user?.picture
    val ghostMode = uiState.settings?.ghostMode ?: false

    LazyColumn(
        modifier = modifier,
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