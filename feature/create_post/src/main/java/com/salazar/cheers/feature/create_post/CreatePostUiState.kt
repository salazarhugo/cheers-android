package com.salazar.cheers.feature.create_post

import android.net.Uri
import androidx.compose.material3.SheetState
import com.mapbox.geojson.Point
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.core.PostType


data class CreatePostUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val audio: LocalAudio? = null,
    val drunkenness: Int = 0,
    val caption: String = "",
    val postType: String = PostType.TEXT,
    val medias: List<Media> = emptyList(),
    val locationPoint: Point? = null,
    val location: String? = null,
    val locationResults: List<String> = emptyList(),
//    val selectedLocation: SearchResult? = null,
    val selectedTagUsers: List<UserItem> = emptyList(),
    val privacyState: SheetState = SheetState(true),
    val privacy: Privacy = Privacy.FRIENDS,
    val allowJoin: Boolean = true,
    val notify: Boolean = true,
    val page: CreatePostPage = CreatePostPage.CreatePost,
    val drinks: List<Drink> = emptyList(),
    val currentDrink: Drink? = null,
    val account: Account? = null,
    val isAudioPlaying: Boolean = false,
    val audioProgress: Float = 0f,
)

enum class CreatePostPage {
    CreatePost, ChooseOnMap, ChooseBeverage, AddPeople, DrunkennessLevel
}
