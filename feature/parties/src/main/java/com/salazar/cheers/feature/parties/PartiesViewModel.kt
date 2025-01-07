package com.salazar.cheers.feature.parties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.domain.feed_party.ListMyPartyFlowUseCase
import com.salazar.cheers.domain.feed_party.ListPartyFeedFlowUseCase
import com.salazar.cheers.domain.feed_party.ListPartyFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartiesUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val query: String = "",
    val parties: List<Party>? = null,
    val myParties: List<Party>? = null,
)

@HiltViewModel
class PartiesViewModel @Inject constructor(
    private val listPartyFeedUseCase: ListPartyFeedUseCase,
    private val listPartyFeedFlowUseCase: ListPartyFeedFlowUseCase,
    private val listMyPartyFlowUseCase: ListMyPartyFlowUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PartiesUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            listMyPartyFlowUseCase().collect(::updateMyParties)
        }
        viewModelScope.launch {
            listPartyFeedFlowUseCase().collect(::updateParties)
        }
        onSwipeToRefresh()
    }

    fun onSwipeToRefresh() {
        updateIsRefreshing(true)
        viewModelScope.launch {
            val result = listPartyFeedUseCase()
            result.onFailure {
                updateError("Couldn't refresh party feed")
            }
            updateIsRefreshing(false)
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    private fun updateError(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message, isLoading = false)
        }
    }

    private fun updateMyParties(parties: List<Party>?) {
        viewModelState.update {
            it.copy(myParties = parties, isLoading = false)
        }
    }

    private fun updateParties(parties: List<Party>?) {
        viewModelState.update {
            it.copy(parties = parties, isLoading = false)
        }
    }

    private fun queryPartys(
        query: String = uiState.value.query.lowercase(),
        fetchFromRemote: Boolean = true,
    ) {
    }

    fun onGoingToggle(party: Party) {
        viewModelScope.launch {
        }
    }

    fun onInterestedToggle(party: Party) {
        viewModelScope.launch {
        }
    }

    fun onErrorMessageChange(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    fun onLoadMore(lastLoadedIndex: Int) {
        val nextItemIndex = lastLoadedIndex + 1
        val nextPage = nextItemIndex / 10 + 1
        viewModelState.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            listPartyFeedUseCase(page = nextPage)
                .onFailure {
                    updateError("Couldn't refresh party feed")
                }
                .onSuccess {
                }
            viewModelState.update { it.copy(isLoadingMore = false) }
        }
    }
}

