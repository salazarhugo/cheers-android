package com.salazar.cheers.feature.home.home

import androidx.compose.foundation.lazy.LazyListState
import cheers.story.v1.UserWithStories
import com.google.android.gms.ads.nativead.NativeAd
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Note
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.Ticket
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.playback.AudioState
import com.salazar.cheers.data.account.Account

data class HomeUiState(
    val currentCity: String = "",
    val selectedPage: HomeSelectedPage = HomeSelectedPage.FRIENDS,
    val posts: List<Post> = emptyList(),
    val tickets: List<Ticket> = emptyList(),
    val spotlight: List<Party> = emptyList(),
    val userWithStoriesList: List<UserWithStories> = emptyList(),
    val listState: LazyListState = LazyListState(),
    val likes: Set<String> = emptySet(),
    val account: Account? = null,
    val suggestions: List<UserItem>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchInput: String = "",
    val nativeAd: NativeAd? = null,
    val notificationCount: Int = 0,
    val endReached: Boolean = false,
    val page: Int = 0,
    val storyPage: Int = 0,
    val storyEndReached: Boolean = false,
    val unreadChatCounter: Int = 0,
    val notes: List<Note> = emptyList(),
    val audioPostID: String = String(),
    val audioState: AudioState = AudioState(),
    val showFloatingActionButton: Boolean = false,
    val isSignedIn: Boolean? = null,
)