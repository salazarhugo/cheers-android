package com.salazar.cheers.feature.map.screens.map

import android.content.Context
import com.mapbox.maps.MapView
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.data.map.UserLocation

sealed class MapUIAction {
    data object OnSwipeRefresh : MapUIAction()
    data object OnBackPressed : MapUIAction()
    data object OnCreatePostClick : MapUIAction()
    data object OnSettingsClick : MapUIAction()
    data object OnPublicToggle : MapUIAction()
    data object OnMyLocationClick : MapUIAction()
    data object OnDismissBottomSheet : MapUIAction()
    data class OnMapReady(val map: MapView, val ctx: Context) : MapUIAction()
    data class OnUserClick(val userID: String) : MapUIAction()
    data class OnChatClick(val userID: String) : MapUIAction()
    data class OnCommentClick(val postID: String) : MapUIAction()
    data class OnUserViewAnnotationClick(val userLocation: com.salazar.cheers.data.map.UserLocation) : MapUIAction()
    data class OnPostViewAnnotationClick(val post: Post) : MapUIAction()
}