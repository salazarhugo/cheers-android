package com.salazar.cheers.ui.main.party.create

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import com.salazar.cheers.R
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.parties.data.repository.PartyRepository
import com.salazar.cheers.workers.CreatePartyWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject


sealed class CreatePartyUIAction {
    object OnShowMapChange : CreatePartyUIAction()
    object OnDismiss : CreatePartyUIAction()
    object OnAddPhoto : CreatePartyUIAction()
    object OnPartyDetailsClick : CreatePartyUIAction()
    object OnDescriptionClick : CreatePartyUIAction()
    object OnLocationClick : CreatePartyUIAction()
    object OnUploadParty : CreatePartyUIAction()
    object OnAddPeopleClick : CreatePartyUIAction()
    object OnHasEndDateToggle : CreatePartyUIAction()
}

data class CreatePartyUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val name: String = "",
    val participants: List<String> = emptyList(),
    val startTimeSeconds: Long = Date().time / 1000,
    val endTimeSeconds: Long = Date().time / 1000,
    val endDate: String = "End date",
    val endTime: String = "End time",
    val address: String = "",
    val photo: Uri? = null,
    val description: String = "",
    val hasEndDate: Boolean = false,
    val showGuestList: Boolean = false,
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationQuery: String = "",
    val locationResults: List<SearchSuggestion> = emptyList(),
    val privacyState: ModalBottomSheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden),
    val privacy: Privacy = Privacy.PUBLIC
)


@HiltViewModel
class CreatePartyViewModel @Inject constructor(
    application: Application,
    private val partyRepository: PartyRepository,
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

    val searchEngine = SearchEngine.createSearchEngine(
            SearchEngineSettings(application.applicationContext.getString(R.string.mapbox_access_token))
        )
    val searchCallback = object : SearchSelectionCallback {
        override fun onCategoryResult(
            suggestion: SearchSuggestion,
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
//                updateResults(results)
        }

        override fun onError(e: Exception) {
            Log.e("GEOCODING", e.toString())
        }

        override fun onResult(
            suggestion: SearchSuggestion,
            result: SearchResult,
            responseInfo: ResponseInfo
        ) {
            val location = result.coordinate!!
            viewModelState.update {
                it.copy(
                    locationName = result.name,
                    latitude = location.latitude(),
                    longitude = location.longitude(),
                    address = result.address?.formattedAddress() ?: "",
                )
            }
        }

        override fun onSuggestions(
            suggestions: List<SearchSuggestion>,
            responseInfo: ResponseInfo
        ) {
            updateResults(suggestions)
        }
    }
    val caption = mutableStateOf("")
    val postType = mutableStateOf(com.salazar.cheers.data.post.repository.PostType.TEXT)


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

    fun onLocationClick(result: SearchSuggestion) {
        searchEngine.select(result, searchCallback)
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

    fun createParty() {
        val state = viewModelState.value

        viewModelScope.launch {
            state.apply {
                val uploadWorkRequest = OneTimeWorkRequestBuilder<CreatePartyWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        workDataOf(
                            "NAME" to name,
                            "ADDRESS" to address,
                            "DESCRIPTION" to description,
                            "EVENT_PRIVACY" to privacy.name,
                            "IMAGE_URI" to photo.toString(),
                            "START_DATETIME" to startTimeSeconds,
                            "END_DATETIME" to endDate,
                            "LOCATION_NAME" to locationName,
                            "LATITUDE" to latitude,
                            "SHOW_GUEST_LIST" to showGuestList,
                            "LONGITUDE" to longitude,
                        )
                    )
                    .build()

                workManager.enqueue(uploadWorkRequest)
            }
        }
    }

    fun getLocations(query: String) {
        val options = SearchOptions(
            limit = 10
        )
        searchEngine.search(query, options, searchCallback)
    }

    fun updateResults(results: List<SearchSuggestion>) {
        viewModelState.update {
            it.copy(locationResults = results)
        }
    }
}
