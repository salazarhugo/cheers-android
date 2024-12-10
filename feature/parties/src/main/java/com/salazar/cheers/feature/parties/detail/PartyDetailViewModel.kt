package com.salazar.cheers.feature.parties.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.WatchStatus
import com.salazar.cheers.data.party.data.repository.PartyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PartyDetailUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoPartys(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PartyDetailUiState

    data class HasParty(
        val party: Party,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PartyDetailUiState
}

private data class PartyDetailViewModelState(
    val party: Party? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): PartyDetailUiState =
        if (party == null) {
            PartyDetailUiState.NoPartys(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            PartyDetailUiState.HasParty(
                party = party,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

@HiltViewModel
class PartyDetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val partyRepository: PartyRepository,
) : ViewModel() {

    private val partyDetailScreen = stateHandle.toRoute<PartyDetailScreen>()
    private val viewModelState = MutableStateFlow(PartyDetailViewModelState(isLoading = true))
    private var partyId: String = partyDetailScreen.partyID

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            partyRepository.getParty(partyId = partyId).collect { party ->
                onPartyChange(party = party)
            }
        }
    }

    private fun fetchParty() {
        viewModelScope.launch {
            partyRepository.getParty(partyId = partyId).collect { party ->
                onPartyChange(party = party)
            }
        }
    }

    private fun onPartyChange(party: Party) {
        viewModelState.update {
            it.copy(party = party)
        }
    }

    fun onWatchStatusChange(watchStatus: WatchStatus) {
        viewModelScope.launch {
            partyRepository.setWatchStatus(
                partyId = partyId,
                watchStatus = watchStatus,
            )
        }
    }

    fun deleteParty() {
        viewModelScope.launch {
//            partyRepository.deleteParty(partyId = partyId)
        }
    }

    fun deleteErrorMessage() {
        viewModelState.update {
            it.copy(errorMessages = emptyList())
        }
    }
}
