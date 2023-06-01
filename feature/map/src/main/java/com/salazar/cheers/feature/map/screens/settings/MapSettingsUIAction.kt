package com.salazar.cheers.feature.map.screens.settings

sealed class MapSettingsUIAction {
    object OnSwipeRefresh : MapSettingsUIAction()
    object OnBackPressed : MapSettingsUIAction()
    data class OnGhostModeChange(val enabled: Boolean) : MapSettingsUIAction()
}