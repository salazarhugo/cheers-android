package com.salazar.cheers.feature.profile.other_profile

sealed class OtherProfileUIAction {
    object OnSwipeRefresh : OtherProfileUIAction()
    object OnBackPressed : OtherProfileUIAction()
    object OnFriendshipClick : OtherProfileUIAction()
    object OnSendMessageClick : OtherProfileUIAction()
    object OnEditProfileClick : OtherProfileUIAction()
    object OnFriendListClick : OtherProfileUIAction()
    object OnGiftClick : OtherProfileUIAction()
    data class OnPostClick(val postID: String): OtherProfileUIAction()
    data class OnSendFriendRequest(val userID: String): OtherProfileUIAction()
    data class OnCancelFriendRequest(val userID: String): OtherProfileUIAction()
    data class OnAcceptFriendRequest(val userID: String): OtherProfileUIAction()
}
