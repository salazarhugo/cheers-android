package com.salazar.cheers.feature.profile.profile

sealed class ProfileUIAction {
    object OnSwipeRefresh : ProfileUIAction()
    object OnBackPressed : ProfileUIAction()
    object OnEditProfileClick : ProfileUIAction()
    object OnFriendListClick : ProfileUIAction()
}
