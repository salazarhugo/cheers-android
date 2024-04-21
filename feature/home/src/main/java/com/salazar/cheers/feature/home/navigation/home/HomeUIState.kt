package com.salazar.cheers.feature.home.navigation.home

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SheetState
import cheers.story.v1.UserWithStories
import com.google.android.gms.ads.nativead.NativeAd
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.playback.AudioState
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.core.model.Note
import com.salazar.cheers.core.Post

data class HomeUiState(
    val posts: List<Post> = emptyList(),
    val userWithStoriesList: List<UserWithStories> = emptyList(),
    val listState: LazyListState = LazyListState(),
    val likes: Set<String> = emptySet(),
    val account: Account? = null,
    val postSheetState: SheetState = SheetState(false),
    val suggestions: List<UserItem>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchInput: String = "",
    val nativeAd: NativeAd? = null,
    val selectedTab: Int = 0,
    val notificationCount: Int = 0,
    val endReached: Boolean = false,
    val page: Int = 0,
    val storyPage: Int = 0,
    val storyEndReached: Boolean = false,
    val unreadChatCounter: Int = 0,
    val notes: List<Note> = emptyList(),
    val audioPostID: String = String(),
    val audioState: AudioState = AudioState(),
)