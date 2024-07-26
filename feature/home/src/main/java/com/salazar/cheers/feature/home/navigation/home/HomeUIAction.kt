package com.salazar.cheers.feature.home.navigation.home

import com.salazar.cheers.core.Post

sealed class HomeUIAction {
    data object OnChatClick : HomeUIAction()
    data object OnActivityClick : HomeUIAction()
    data object OnSearchClick : HomeUIAction()
    data object OnSwipeRefresh : HomeUIAction()
    data object OnAddStoryClick : HomeUIAction()
    data object OnCreatePostClick : HomeUIAction()
    data object OnPartiesClick : HomeUIAction()
    data object OnLoadNextItems : HomeUIAction()
    data object OnCreateNoteClick : HomeUIAction()
    data class OnPostCommentClick(val postID: String) : HomeUIAction()
    data class OnDeletePostClick(val postID: String) : HomeUIAction()
    data class OnPostLikesClick(val postID: String) : HomeUIAction()
    data class OnShareClick(val postID: String) : HomeUIAction()
    data class OnLikeClick(val post: Post) : HomeUIAction()
    data class OnStoryFeedClick(val page: Int) : HomeUIAction()
    data class OnStoryClick(val userID: String) : HomeUIAction()
    data class OnUserClick(val userID: String) : HomeUIAction()
    data class OnAudioClick(
        val postID: String,
        val audioUrl: String,
    ) : HomeUIAction()
    data class OnPostClick(val postID: String) : HomeUIAction()
    data class OnNoteClick(val userID: String) : HomeUIAction()
    data class OnAddFriendClick(val userID: String) : HomeUIAction()
}