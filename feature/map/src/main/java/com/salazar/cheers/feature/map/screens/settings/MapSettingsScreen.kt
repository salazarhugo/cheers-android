package com.salazar.cheers.feature.map.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.map.ui.GhostModeCard

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
        modifier = Modifier
            .fillMaxWidth()
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