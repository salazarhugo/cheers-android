package com.salazar.cheers.feature.profile.other_profile

import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.UserID

sealed class OtherProfileUIAction {
    data object OnSwipeRefresh : OtherProfileUIAction()
    data object OnBackPressed : OtherProfileUIAction()
    data object OnFriendshipClick : OtherProfileUIAction()
    data object OnSendMessageClick : OtherProfileUIAction()
    data object OnEditProfileClick : OtherProfileUIAction()
    data object OnFriendListClick : OtherProfileUIAction()
    data object OnGiftClick : OtherProfileUIAction()
    data class OnPostClick(val postID: String): OtherProfileUIAction()
    data class OnSendFriendRequest(val userID: String): OtherProfileUIAction()
    data class OnCancelFriendRequest(val userID: String): OtherProfileUIAction()
    data class OnAcceptFriendRequest(val userID: String): OtherProfileUIAction()
    data class OnLikeClick(val post: Post): OtherProfileUIAction()
    data class OnUserClick(val userID: UserID): OtherProfileUIAction()
    data class OnLikeCountClick(val postID: String): OtherProfileUIAction()
    data class OnCommentClick(val postID: String): OtherProfileUIAction()
}
