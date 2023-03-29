package com.salazar.cheers.map.screens.settings

sealed class MapSettingsUIAction {
    object OnSwipeRefresh : MapSettingsUIAction()
    object OnBackPressed : MapSettingsUIAction()
    data class OnGhostModeChange(val enabled: Boolean) : MapSettingsUIAction()
}