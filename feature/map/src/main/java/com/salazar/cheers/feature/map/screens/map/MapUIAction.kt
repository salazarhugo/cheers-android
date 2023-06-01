package com.salazar.cheers.feature.map.screens.map

import android.content.Context
import com.mapbox.maps.MapView
import com.salazar.cheers.feature.map.domain.models.UserLocation

sealed class MapUIAction {
    object OnSwipeRefresh : MapUIAction()
    object OnBackPressed : MapUIAction()
    object OnCreatePostClick : MapUIAction()
    object OnSettingsClick : MapUIAction()
    object OnPublicToggle : MapUIAction()
    object OnMyLocationClick : MapUIAction()
    data class OnMapReady(val map: MapView, val ctx: Context) : MapUIAction()
    data class OnUserClick(val userID: String) : MapUIAction()

    //    data class OnPostClick(val post: Post) : MapUIAction()
    data class OnChatClick(val userID: String) : MapUIAction()
    data class OnCommentClick(val postID: String) : MapUIAction()
    data class OnUserViewAnnotationClick(val userLocation: UserLocation) : MapUIAction()
}