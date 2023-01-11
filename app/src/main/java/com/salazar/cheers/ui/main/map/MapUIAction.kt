package com.salazar.cheers.ui.main.map

import android.content.Context
import com.mapbox.maps.MapView
import com.salazar.cheers.internal.Post

sealed class MapUIAction {
    object OnSwipeRefresh : MapUIAction()
    object OnBackPressed : MapUIAction()
    object OnCreatePostClick : MapUIAction()
    object OnSettingsClick : MapUIAction()
    object OnPublicToggle : MapUIAction()
    object OnMyLocationClick : MapUIAction()
    data class OnMapReady(val map: MapView, val ctx: Context) : MapUIAction()
    data class OnUserClick(val userID: String) : MapUIAction()
    data class OnPostClick(val post: Post) : MapUIAction()
}