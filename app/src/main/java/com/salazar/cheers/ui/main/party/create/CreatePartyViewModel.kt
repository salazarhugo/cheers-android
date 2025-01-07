package com.salazar.cheers.ui.main.party.create

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.SearchSuggestion
import com.salazar.cheers.core.util.Constants
import com.salazar.cheers.domain.get_last_known_location.GetLastKnownLocationUseCase
import com.salazar.cheers.domain.get_location_name.GetLocationNameUseCase
import com.salazar.cheers.domain.list_search_location.ListSearchLocationUseCase
import com.salazar.cheers.shared.util.result.getOrNull
import com.salazar.cheers.workers.CreatePartyWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class CreatePartyViewModel @Inject constructor(
    application: Application,
    internal val listSearchLocationUseCase: ListSearchLocationUseCase,
    internal val getLocationNameUseCase: GetLocationNameUseCase,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatePartyUiState(isLoading = false))
    private val workManager = WorkManager.getInstance(application)
    private var searchJob: Job? = null

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            val location = getLastKnownLocationUseCase() ?: return@launch
            getLocationNameUseCase(
                location.longitude,
                location.latitude,
            ).getOrNull()?.let {
                updateResults(
                    results = it.map {
                        SearchSuggestion(
                            name = it,
                            address = it,
                            latitude = location.latitude,
                            longitude = location.latitude,
                        )
                    },
                )
            }
        }
    }

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

    fun onShowGuestListToggle() {
        viewModelState.update {
            it.copy(showGuestList = !it.showGuestList)
        }
    }

    fun onLocationClick(location: SearchSuggestion) {
        viewModelState.update {
            it.copy(
                locationName = location.name,
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address.orEmpty(),
                city = location.city,
            )
        }
    }

    fun onDescriptionChange(description: String) {
        viewModelState.update {
            it.copy(description = description)
        }
    }

    fun onQueryChange(query: String) {
        viewModelState.update {
            it.copy(locationQuery = query)
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            getLocations(query)
        }
    }

    fun onStartTimeSecondsChange(dateMillis: Long) {
        viewModelState.update {
            if (it.endDateTimeMillis < dateMillis) {
                onEndTimeSecondsChange(dateMillis + TimeUnit.HOURS.toMillis(7))
            }
            it.copy(startDateTimeMillis = dateMillis)
        }
    }

    fun onEndTimeSecondsChange(dateMillis: Long) {
        viewModelState.update {
            it.copy(endDateTimeMillis = dateMillis)
        }
    }

    fun onNameChange(name: String) {
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

    fun createParty() {
        val state = viewModelState.value

        viewModelScope.launch {
            state.apply {
                val imageUri = photo?.toString() ?: ""
                val uploadWorkRequest = OneTimeWorkRequestBuilder<CreatePartyWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        workDataOf(
                            "NAME" to name,
                            "ADDRESS" to address,
                            "CITY" to city,
                            "DESCRIPTION" to description,
                            "EVENT_PRIVACY" to privacy.name,
                            "IMAGE_URI" to imageUri,
                            "START_DATETIME" to startDateTimeMillis / 1000,
                            "END_DATETIME" to endDateTimeMillis / 1000,
                            "LATITUDE" to latitude,
                            "SHOW_GUEST_LIST" to showGuestList,
                            "LONGITUDE" to longitude,
                        )
                    )
                    .build()

                workManager.enqueueUniqueWork(
                    Constants.PARTY_UNIQUE_WORKER_NAME,
                    ExistingWorkPolicy.REPLACE,
                    uploadWorkRequest
                )
            }
        }
    }

    private fun getLocations(query: String) {
        viewModelScope.launch {
            val result = listSearchLocationUseCase(query).getOrNull() ?: return@launch
            updateResults(result)
        }
    }

    private fun updateResults(results: List<SearchSuggestion>) {
        viewModelState.update {
            it.copy(locationResults = results)
        }
    }

    fun onPrivacyChange(privacy: Privacy) {
        viewModelState.update {
            it.copy(privacy = privacy)
        }
    }
}
