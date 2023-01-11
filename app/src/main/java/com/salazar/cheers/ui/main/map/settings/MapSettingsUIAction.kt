package com.salazar.cheers.ui.main.map.settings

sealed class MapSettingsUIAction {
    object OnSwipeRefresh : MapSettingsUIAction()
    object OnBackPressed : MapSettingsUIAction()
}