package com.salazar.cheers.ui.main.party.guestlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.party.PartyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuestListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val interested: List<UserItem>? = null,
    val going: List<UserItem>? = null,
)

@HiltViewModel
class GuestListViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val partyRepository: PartyRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(GuestListUiState(isLoading = true))
    lateinit var eventId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("eventId")?.let {
            eventId = it
        }

        load()
    }

    private fun load() {
        viewModelScope.launch {
//            partyRepository.interestedList(eventId = eventId)?.let {
//                updateInterested(users = it)
//            }
        }

        viewModelScope.launch {
//            partyRepository.goingList(eventId = eventId)?.let {
//                updateGoing(users = it)
//            }
        }
    }

    private fun updateInterested(users: List<UserItem>) {
        viewModelState.update {
            it.copy(interested = users)
        }
    }

    private fun updateGoing(users: List<UserItem>) {
        viewModelState.update {
            it.copy(going = users)
        }
    }

    fun onSwipeRefresh() {
        load()
    }
}

