package com.salazar.cheers.feature.profile.profile

sealed class ProfileUIAction {
    data object OnSwipeRefresh : ProfileUIAction()
    data object OnBackPressed : ProfileUIAction()
    data object OnEditProfileClick : ProfileUIAction()
    data object OnFriendListClick : ProfileUIAction()
    data class OnPostMoreClick(val postID: String) : ProfileUIAction()
    data class OnPostDetailsClick(val postID: String) : ProfileUIAction()
    data class OnUserClick(val userID: String) : ProfileUIAction()
}
