package com.salazar.cheers.ui.main.party.guestlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.shared.util.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuestListUiState(
    val isRefreshing: Boolean = false,
    val errorMessage: String = "",
    val interested: List<UserItem>? = null,
    val going: List<UserItem>? = null,
)

@HiltViewModel
class GuestListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val partyRepository: PartyRepository,
) : ViewModel() {

    private val guestListScreen = savedStateHandle.toRoute<GuestListScreen>()
    private val viewModelState = MutableStateFlow(GuestListUiState(isRefreshing = false))

    val goingState =
        MutableStateFlow<GuestListGoingState>(GuestListGoingState.Loading)

    val interestedState =
        MutableStateFlow<GuestListGoingState>(GuestListGoingState.Loading)

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        load()
    }

    private fun load() {
        viewModelState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            interestedState.update { GuestListGoingState.Loading }
            val result = partyRepository.listInterested(partyID = guestListScreen.partyID)
            when (result) {
                is Result.Error -> {}
                is Result.Success -> updateInterested(users = result.data)
            }
        }

        viewModelScope.launch {
            goingState.update { GuestListGoingState.Loading }
            val result = partyRepository.listGoing(partyID = guestListScreen.partyID)
            when (result) {
                is Result.Error -> {}
                is Result.Success -> updateGoing(users = result.data)
            }
            viewModelState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun updateInterested(users: List<UserItem>) {
        interestedState.update {
            GuestListGoingState.Users(users)
        }
    }

    private fun updateGoing(users: List<UserItem>) {
        goingState.update {
            GuestListGoingState.Users(users)
        }
    }

    fun onSwipeRefresh() {
        load()
    }
}

