package com.salazar.cheers.ui.main.event

import android.app.Application
import android.net.Uri
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.Privacy
import com.salazar.cheers.internal.User
import com.salazar.cheers.workers.UploadEventWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject


data class AddEventUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val name: String = "",
    val participants: List<String> = emptyList(),
    val startDate: String = "Start date",
    val startTime: String = "Start time",
    val endDate: String = "End date",
    val endTime: String = "End time",
    val imageUri: Uri? = null,
    val description: String = "",
    val allDay: Boolean = false,
    val privacyState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val selectedPrivacy: Privacy = Privacy.FRIENDS
)


@HiltViewModel
class AddEventViewModel @Inject constructor(application: Application) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    private val viewModelState = MutableStateFlow(AddEventUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    val caption = mutableStateOf("")

    val postType = mutableStateOf(PostType.TEXT)

    val videoThumbnail = mutableStateOf<Uri?>(null)

    val location = mutableStateOf("Current Location")
    val locationResults = mutableStateOf<List<SearchResult>>(emptyList())
    val selectedLocation = mutableStateOf<SearchResult?>(null)
    val selectedTagUsers = mutableStateListOf<User>()
    val showOnMap = mutableStateOf(true)

    fun selectPrivacy(privacy: Privacy) {
        viewModelState.update {
            it.copy(selectedPrivacy = privacy)
        }
    }

    fun onAllDayChange(allDay: Boolean) {
        viewModelState.update {
            it.copy(allDay = allDay)
        }
    }

    fun onDescriptionChange(description: String) {
        viewModelState.update {
            it.copy(description = description)
        }
    }

    fun onShowOnMapChanged(showOnMap: Boolean) {
        this.showOnMap.value = showOnMap
    }

    fun onEndTimeChange(endTime: String) {
        viewModelState.update {
            it.copy(endTime = endTime)
        }
    }

    fun onEndDateChange(endDate: String) {
        viewModelState.update {
            it.copy(endDate = endDate)
        }
    }

    fun onStartTimeChange(startTime: String) {
        viewModelState.update {
            it.copy(startTime = startTime)
        }
    }

    fun onStartDateChange(startDate: String) {
        viewModelState.update {
            it.copy(startDate = startDate)
        }
    }

    fun onEventNameChange(eventName: String) {
        viewModelState.update {
            it.copy(name = eventName)
        }
    }

    fun selectTagUser(user: User) {
        if (selectedTagUsers.contains(user))
            selectedTagUsers.remove(user)
        else
            selectedTagUsers.add(user)
    }

    fun unselectLocation() {
        selectedLocation.value = null
    }

    fun selectLocation(location: SearchResult) {
        selectedLocation.value = location
    }

    fun updateLocationResults(results: List<SearchResult>) {
        this.locationResults.value = results
    }

    fun onCaptionChanged(caption: String) {
        this.caption.value = caption
    }

    fun setImage(imageUri: Uri) {
        viewModelState.update {
            it.copy(imageUri = imageUri)
        }
    }

    var uploadWorkerState: Flow<WorkInfo>? = null
    val id = mutableStateOf<UUID?>(null)

    fun uploadEvent() {
        val state = viewModelState.value

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadEventWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "NAME" to state.name,
                        "DESCRIPTION" to state.description,
                        "LOCATION_NAME" to selectedLocation.value?.name,
                        "LOCATION_LATITUDE" to selectedLocation.value?.coordinate?.latitude(),
                        "LOCATION_LONGITUDE" to selectedLocation.value?.coordinate?.longitude(),
                        "PARTICIPANTS" to state.participants.toTypedArray(),
                        "SHOW_ON_MAP" to showOnMap.value,
                        "EVENT_TYPE" to state.selectedPrivacy.name,
                        "IMAGE_URI" to state.imageUri.toString(),
                        "START_DATETIME" to "${state.startDate}T${state.startTime}",
                        "END_DATETIME" to "${state.endDate}T${state.endTime}",
                    )
                )
            }
                .build()

        id.value = uploadWorkRequest.id

        // Actually start the work
        workManager.enqueue(uploadWorkRequest)

        uploadWorkerState = workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).asFlow()
    }


    fun updateLocation(location: String) {
        this.location.value = location
    }
}
