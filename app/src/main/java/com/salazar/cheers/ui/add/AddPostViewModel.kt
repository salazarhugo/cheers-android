package com.salazar.cheers.ui.add

import android.app.Application
import android.net.Uri
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.event.PrivacyItem
import com.salazar.cheers.workers.UploadPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

enum class Privacy {
    NONE, PRIVATE, FRIENDS, PUBLIC, GROUP
}

data class AddPostUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val caption: String = "",
    val beverage: String = "",
    val postType: String = PostType.TEXT,
    val mediaUri: Uri? = null,
    val locationPoint: Point? = null,
    val location: String = "Current Location",
    val locationResults: List<SearchResult> = emptyList(),
    val selectedLocation: SearchResult? = null,
    val selectedTagUsers: List<User> = emptyList(),
    val privacyState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val privacy: PrivacyItem = PrivacyItem(
        "Privacy",
        "Choose a privacy",
        Icons.Filled.Lock,
        Privacy.NONE
    ),
    val showOnMap: Boolean = true,
    val isChooseOnMapOpen: Boolean = false,
    val isChooseBeverageOpen: Boolean = false,
)

@HiltViewModel
class AddPostViewModel @Inject constructor(application: Application) : ViewModel() {

    private val viewModelState = MutableStateFlow(AddPostUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    private val workManager = WorkManager.getInstance(application)

    fun onShowOnMapChanged(showOnMap: Boolean) {
        viewModelState.update {
            it.copy(showOnMap = showOnMap)
        }
    }

    fun interactedWithChooseOnMap() {
        viewModelState.update {
            it.copy(isChooseOnMapOpen = true)
        }
    }

    fun interactedWithChooseBeverage() {
        viewModelState.update {
            it.copy(isChooseBeverageOpen = true)
        }
    }

    fun interactedWithAddPost() {
        viewModelState.update {
            it.copy(isChooseOnMapOpen = false, isChooseBeverageOpen = false)
        }
    }

    fun selectPrivacy(privacy: PrivacyItem) {
        viewModelState.update {
            it.copy(privacy = privacy)
        }
    }

    fun selectTagUser(user: User) {
        val l = viewModelState.value.selectedTagUsers.toMutableList()
        if (l.contains(user)) l.remove(user) else l.add(user)
        viewModelState.update {
            it.copy(selectedTagUsers = l.toList())
        }
    }

    fun unselectLocation() {
        viewModelState.update {
            it.copy(selectedLocation = null)
        }
    }

    fun selectLocation(location: SearchResult) {
        viewModelState.update {
            it.copy(selectedLocation = location)
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

    fun updateLocationResults(results: List<SearchResult>) {
        viewModelState.update {
            it.copy(locationResults = results)
        }
    }

    fun onCaptionChanged(caption: String) {
        viewModelState.update {
            it.copy(caption = caption)
        }
    }

    fun setPostImage(image: Uri) {
        viewModelState.update {
            it.copy(mediaUri = image, postType = PostType.IMAGE)
        }
    }

    fun setPostVideo(video: Uri) {
        viewModelState.update {
            it.copy(mediaUri = video, postType = PostType.VIDEO)
        }
    }

    var uploadWorkerState: Flow<WorkInfo>? = null
    val id = mutableStateOf<UUID?>(null)

    fun uploadPost() {
        val uiState = viewModelState.value
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadPostWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "MEDIA_URI" to uiState.mediaUri.toString(),
                        "POST_TYPE" to uiState.postType,
                        "PHOTO_CAPTION" to uiState.caption,
                        "LOCATION_NAME" to uiState.selectedLocation?.name,
                        "LOCATION_LATITUDE" to uiState.selectedLocation?.coordinate?.latitude(),
                        "LOCATION_LONGITUDE" to uiState.selectedLocation?.coordinate?.longitude(),
                        "TAG_USER_IDS" to uiState.selectedTagUsers.map { it.id }.toTypedArray(),
                        "PRIVACY" to uiState.privacy.type.name,
                    )
                )
            }
                .build()

        id.value = uploadWorkRequest.id
        workManager.enqueue(uploadWorkRequest)
        uploadWorkerState = workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).asFlow()
    }
}

