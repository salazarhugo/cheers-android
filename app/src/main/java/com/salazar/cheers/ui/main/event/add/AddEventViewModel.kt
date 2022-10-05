package com.salazar.cheers.ui.main.event.add

import android.net.Uri
import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import com.salazar.cheers.data.repository.PartyRepository
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.Privacy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    val startTimeSeconds: Long = Date().time / 1000 + 86400,
    val endTimeSeconds: Long = Date().time / 1000 + 86400,
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
    val privacy: Privacy = Privacy.FRIENDS
)


@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val partyRepository: PartyRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(AddEventUiState(isLoading = false))
    private var searchJob: Job? = null

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    val searchEngine = MapboxSearchSdk.getSearchEngine()
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

    fun uploadEvent() {
        val state = viewModelState.value

        viewModelScope.launch {
            state.apply {
                partyRepository.createParty(
                    Party().copy(
                        name = name,
                        address = address,
                        description = description,
                        showGuestList = showGuestList,
                        privacy = privacy,
                        startDate = startTimeSeconds,
                        endDate = endTimeSeconds,
                        bannerUrl = photo.toString(),
                        locationName = locationName,
                        latitude = latitude,
                        longitude = longitude,
                    ),
                )
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
