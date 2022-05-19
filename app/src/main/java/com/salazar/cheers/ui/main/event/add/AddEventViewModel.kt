package com.salazar.cheers.ui.main.event.add

import android.net.Uri
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.Privacy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject


sealed class AddEventUIAction {
    object OnShowMapChange : AddEventUIAction()
    object OnDismiss : AddEventUIAction()
    object OnAddPhoto : AddEventUIAction()
    object OnEventDetailsClick : AddEventUIAction()
    object OnDescriptionClick : AddEventUIAction()
    object OnLocationClick : AddEventUIAction()
    object OnUploadEvent : AddEventUIAction()
    object OnAddPeopleClick : AddEventUIAction()
    object OnHasEndDateToggle : AddEventUIAction()
}

data class AddEventUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val name: String = "",
    val participants: List<String> = emptyList(),
    val startTimeSeconds: Long = Date().time/1000 + 86400,
    val endTimeSeconds: Long = Date().time/1000 + 86400,
    val endDate: String = "End date",
    val endTime: String = "End time",
    val photo: Uri? = null,
    val description: String = "",
    val hasEndDate: Boolean = false,
    val privacyState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val privacy: Privacy = Privacy.FRIENDS
)


@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(AddEventUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    val caption = mutableStateOf("")
    val postType = mutableStateOf(PostType.TEXT)


    fun setPhoto(photo: Uri?) {
        if (photo == null) return
        viewModelState.update {
            it.copy(photo = photo)
        }
    }

    fun setPrivacy(privacy: Privacy) {
        viewModelState.update {
            it.copy(privacy = privacy)
        }
    }

    fun onDescriptionChange(description: String) {
        viewModelState.update {
            it.copy(description = description)
        }
    }

    fun onStartTimeSecondsChange(epoch: Long) {
        viewModelState.update {
            it.copy(startTimeSeconds = epoch)
        }
    }

    fun onEndTimeSecondsChange(epoch: Long) {
        viewModelState.update {
            it.copy(endTimeSeconds = epoch)
        }
    }

    fun setName(name: String) {
        viewModelState.update {
            it.copy(name = name)
        }
    }

    fun hasEndDateToggle() {
        viewModelState.update {
            it.copy(hasEndDate = !it.hasEndDate)
        }
    }


    val id = mutableStateOf<UUID?>(null)

    fun uploadEvent() {
        val state = viewModelState.value

        eventRepository.createEvent(
            name = state.name,
            description = state.description,
            privacy = state.privacy,
            startTimeSeconds = state.startTimeSeconds,
            endTimeSeconds = state.endTimeSeconds,
            imageUri = state.photo ?: Uri.EMPTY,
        )
    }


    fun updateLocation(location: String) {
//        this.location.value = location
    }
}
