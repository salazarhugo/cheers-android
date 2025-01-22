package com.salazar.cheers.feature.create_post

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mapbox.geojson.Point
import com.salazar.cheers.core.PostType
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.toMedia
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.core.util.playback.AndroidAudioPlayer
import com.salazar.cheers.shared.util.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreatePostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createPostUseCases: CreatePostUseCases,
    private val audioPlayer: AndroidAudioPlayer,
) : ViewModel() {

    private val createPost = savedStateHandle.toRoute<CreatePostGraph>()
    private val viewModelState = MutableStateFlow(
        CreatePostUiState(
            isLoading = false,
//            medias = if (createPost.uri != null) listOf(Media.Image(createPost.uri))
//            else emptyList(),
        )
    )

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            audioPlayer.isPlayingFlow.collect(::updateIsAudioPlaying)
        }

        viewModelScope.launch {
            audioPlayer.currentPositionFlow().collect(::updateAudioProgress)
        }

        viewModelScope.launch {
            val account = createPostUseCases.getAccountUseCase().first()
            viewModelState.update {
                it.copy(account = account)
            }
        }

        viewModelScope.launch {
            val location = createPostUseCases.getLastKnownLocationUseCase() ?: return@launch
            updateLocationPoint(Point.fromLngLat(location.longitude, location.latitude))
        }
    }


    fun onAudioClick() {
        val audio = uiState.value.audio ?: return
        viewModelScope.launch(Dispatchers.IO) {
            audioPlayer.playLocalAudio(audio)
        }
    }

    fun selectDrink(drink: Drink) {
        viewModelState.update {
            it.copy(currentDrink = drink)
        }
    }

    fun selectPrivacy(privacy: Privacy) {
        viewModelState.update {
            it.copy(privacy = privacy)
        }
    }

    fun selectTagUser(user: UserItem) {
        val l = viewModelState.value.selectedTagUsers.toMutableList()
        if (l.contains(user)) l.remove(user) else l.add(user)
        viewModelState.update {
            it.copy(selectedTagUsers = l.toList())
        }
    }

    private fun getLocationName(
        longitude: Double,
        latitude: Double,
        zoom: Double,
    ) {
        viewModelScope.launch {
            val result = createPostUseCases.getLocationNameUseCase(
                longitude = longitude,
                latitude = latitude,
                zoom = zoom,
            )
            when (result) {
                is Result.Error -> {}
                is Result.Success -> {
                    viewModelState.update {
                        it.copy(locationResults = result.data)
                    }
                }
            }
        }
    }

    fun unselectLocation() {
//        viewModelState.update {
//            it.copy(selectedLocation = null)
//        }
    }

    fun onDrunkennessChange(drunkenness: Int) {
        viewModelState.update {
            it.copy(drunkenness = drunkenness)
        }
    }

//    fun selectLocation(location: SearchResult) {
//        viewModelState.update {
//            it.copy(selectedLocation = location)
//        }
//    }

    fun onEnabledLikesChange(enabled: Boolean) {
        viewModelState.update {
            it.copy(likesEnabled = enabled)
        }
    }

    fun onEnableShareChange(enabled: Boolean) {
        viewModelState.update {
            it.copy(shareEnabled = enabled)
        }
    }

    fun onEnableCommentsChange(enabled: Boolean) {
        viewModelState.update {
            it.copy(commentsEnabled = enabled)
        }
    }

    fun toggleNotify(notify: Boolean) {
        viewModelState.update {
            it.copy(notify = notify)
        }
    }

    private fun updateFriends(friends: List<UserItem>) {
        viewModelState.update {
            it.copy(friends = friends)
        }
    }

    private fun updateIsAudioPlaying(isAudioPlaying: Boolean) {
        viewModelState.update {
            it.copy(isAudioPlaying = isAudioPlaying)
        }
    }

    private fun updateAudioProgress(progress: Float) {
        viewModelState.update {
            it.copy(audioProgress = progress)
        }
    }

    private fun updateErrorMessage(errorMessage: String) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    fun updatePage(page: CreatePostPage) {
        viewModelState.update {
            it.copy(page = page)
        }
    }

    fun updateLocationPoint(point: Point, zoom: Double = 10.0) {
        viewModelState.update {
            it.copy(locationPoint = point)
        }
        getLocationName(
            point.longitude(),
            point.latitude(),
            zoom,
        )
    }

    fun updateLocation(location: String?) {
        viewModelState.update {
            it.copy(location = location)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

//    fun updateLocationResults(results: List<SearchResult>) {
//        viewModelState.update {
//            it.copy(locationResults = results)
//        }
//    }

    fun onCaptionChanged(caption: String) {
        viewModelState.update {
            it.copy(caption = caption)
        }
    }

    fun addAudio(audio: LocalAudio?) {
        viewModelState.update {
            it.copy(
                audio = audio,
            )
        }
    }

    fun addPhoto(context: Context, photo: Uri) {
        viewModelState.update {
            it.copy(
                medias = it.medias + photo.toMedia(context),
                postType = PostType.IMAGE
            )
        }
    }

    fun setMedia(
        context: Context,
        photos: List<Uri>,
    ) {
        viewModelState.update {
            it.copy(
                medias = photos.toMedia(context),
                postType = PostType.IMAGE
            )
        }
    }

    fun uploadPost() {
        val uiState = viewModelState.value
        val drinkID = uiState.currentDrink?.id.orEmpty()
        val localAudio = uiState.audio
        val location = uiState.location.orEmpty()

        updateIsLoading(true)

        viewModelScope.launch {
            createPostUseCases.createPostUseCase(
                "PHOTOS" to uiState.medias.filterIsInstance<Media.Image>().map { it.uri.toString() }
                    .toTypedArray(),
                "AUDIO_URI" to localAudio?.uri.toString(),
                "AUDIO_AMPLITUDES" to localAudio?.amplitudes?.toTypedArray(),
                "POST_TYPE" to uiState.postType,
                "PHOTO_CAPTION" to uiState.caption,
                "DRUNKENNESS" to uiState.drunkenness,
                "DRINK_ID" to drinkID,
                "LOCATION_NAME" to location,
                "LOCATION_LATITUDE" to uiState.locationPoint?.latitude(),
                "LOCATION_LONGITUDE" to uiState.locationPoint?.longitude(),
                "TAG_USER_IDS" to uiState.selectedTagUsers.map { it.id }.toTypedArray(),
                "PRIVACY" to uiState.privacy.name,
                "NOTIFY" to uiState.notify,
                "LIKES_ENABLED" to uiState.likesEnabled,
                "COMMENTS_ENABLED" to uiState.commentsEnabled,
                "SHARE_ENABLED" to uiState.shareEnabled,
            )
        }

        updateIsLoading(false)
    }
}