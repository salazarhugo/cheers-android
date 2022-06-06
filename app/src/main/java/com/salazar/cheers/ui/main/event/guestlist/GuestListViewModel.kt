package com.salazar.cheers.ui.main.event.guestlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.EventRepository
import com.salazar.cheers.internal.User
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
    val interested: List<User>? = null,
    val going: List<User>? = null,
)

@HiltViewModel
class GuestListViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
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
            eventRepository.interestedList(eventId = eventId)?.let {
                updateInterested(users = it)
            }
        }

        viewModelScope.launch {
            eventRepository.goingList(eventId = eventId)?.let {
                updateGoing(users = it)
            }
        }
    }

    private fun updateInterested(users: List<User>) {
        viewModelState.update {
            it.copy(interested = users)
        }
    }

    private fun updateGoing(users: List<User>) {
        viewModelState.update {
            it.copy(going = users)
        }
    }

    fun onSwipeRefresh() {
        load()
    }
}

