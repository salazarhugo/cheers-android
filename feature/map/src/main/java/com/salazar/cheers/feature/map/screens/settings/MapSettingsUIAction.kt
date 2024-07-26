package com.salazar.cheers.feature.map.screens.settings

sealed class MapSettingsUIAction {
    data object OnSwipeRefresh : MapSettingsUIAction()
    data object OnBackPressed : MapSettingsUIAction()
    data class OnGhostModeChange(val enabled: Boolean) : MapSettingsUIAction()
}