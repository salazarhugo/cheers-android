package com.salazar.cheers.feature.create_post

import android.content.Context
import android.net.Uri
import androidx.compose.material3.SheetState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Media
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.toMedia
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.core.util.playback.AndroidAudioPlayer
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.data.post.repository.PostType
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
    stateHandle: SavedStateHandle,
    private val createPostUseCases: CreatePostUseCases,
    private val audioPlayer: AndroidAudioPlayer,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatePostUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("photoUri")?.let {
//            addPhoto(Uri.parse(it))
        }

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

        viewModelScope.launch {
            createPostUseCases.listDrinkUseCase().collect { result ->
                result.onSuccess { drinks ->
                    updateDrinks(drinks)
                }.onFailure {
                    updateErrorMessage(it.localizedMessage)
                }
            }
        }
    }


    fun onAudioClick() {
        val audio = uiState.value.audio ?: return
        viewModelScope.launch(Dispatchers.IO) {
            audioPlayer.playLocalAudio(audio)
        }
    }

    private fun updateDrinks(drinks: List<Drink>) {
        val emptyDrink = listOf(
            Drink(
                id = 0,
                name = "",
                icon = "",
                category = "",
            )
        )
        viewModelState.update {
            it.copy(drinks = emptyDrink + drinks)
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

    fun getLocationName(
        longitude: Double,
        latitude: Double,
        zoom: Double,
    ) {
        viewModelScope.launch {
            createPostUseCases.getLocationNameUseCase(
                longitude = longitude,
                latitude = latitude,
                zoom = zoom,
            ).collect { names ->
                viewModelState.update {
                    it.copy(locationResults = names)
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

    fun toggleNotify(notify: Boolean) {
        viewModelState.update {
            it.copy(notify = notify)
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

    fun updateLocationPoint(point: Point) {
        viewModelState.update {
            it.copy(locationPoint = point)
        }
    }

    fun updateLocation(location: String) {
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
        val drinkID = uiState.currentDrink?.id ?: 0
        val localAudio = uiState.audio
        updateIsLoading(true)

        viewModelScope.launch {
            createPostUseCases.createPostUseCase(
                "PHOTOS" to uiState.medias.filterIsInstance(Media.Image::class.java).map { it.uri.toString() }.toTypedArray(),
                "AUDIO_URI" to localAudio?.uri.toString(),
                "AUDIO_AMPLITUDES" to localAudio?.amplitudes?.toTypedArray(),
                "POST_TYPE" to uiState.postType,
                "PHOTO_CAPTION" to uiState.caption,
                "DRUNKENNESS" to uiState.drunkenness,
                "DRINK_ID" to drinkID.toLong(),
                "LOCATION_NAME" to "",
                "LOCATION_LATITUDE" to uiState.locationPoint?.latitude(),
                "LOCATION_LONGITUDE" to uiState.locationPoint?.longitude(),
                "TAG_USER_IDS" to uiState.selectedTagUsers.map { it.id }.toTypedArray(),
                "PRIVACY" to uiState.privacy.name,
                "NOTIFY" to uiState.notify,
            )
        }

        updateIsLoading(false)
    }
}