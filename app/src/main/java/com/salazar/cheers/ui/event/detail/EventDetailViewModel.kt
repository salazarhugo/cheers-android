package com.salazar.cheers.ui.event.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.EventUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface EventDetailUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoEvents(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : EventDetailUiState

    data class HasEvent(
        val eventUi: EventUi,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : EventDetailUiState
}

private data class EventDetailViewModelState(
    val eventUi: EventUi? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): EventDetailUiState =
        if (eventUi == null) {
            EventDetailUiState.NoEvents(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            EventDetailUiState.HasEvent(
                eventUi = eventUi,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

class EventDetailViewModel @AssistedInject constructor(
//    private val repository: Neo4jRepository,
    @Assisted private val eventId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EventDetailViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshEvent()
    }

    private fun refreshEvent() {
        viewModelScope.launch {
            val result = Neo4jUtil.getEvent(eventId = eventId)
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(eventUi = result.data)
                    is Result.Error -> it.copy(errorMessages = listOf(result.exception.toString()))
                }
            }
        }
    }

//    private fun unlikeEvent(eventId: String) {
//        viewModelScope.launch {
//            try {
//                Neo4jUtil.unlikeEvent(eventId = eventId)
//            } catch (e: Exception) {
//                Log.e("EventDetailViewModel", e.toString())
//            }
//        }
//    }

//    private fun likeEvent(eventId: String) {
//        viewModelScope.launch {
//            try {
//                Neo4jUtil.likeEvent(eventId = eventId)
//            } catch (e: Exception) {
//                Log.e("EventDetailViewModel", e.toString())
//            }
//        }
//    }

    fun selectEvent(eventId: String) {
//        viewModelState.update {
//            it.copy(selectedEventId = eventId)
//        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            try {
//                Neo4jUtil.deleteEvent(eventId = eventId)
            } catch (e: Exception) {
                Log.e("EventDetailViewModel", e.toString())
            }
        }
    }

    fun deleteErrorMessage() {
        viewModelState.update {
            it.copy(errorMessages = emptyList())
        }
    }

    @AssistedFactory
    interface EventDetailViewModelFactory {
        fun create(eventId: String): EventDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: EventDetailViewModelFactory,
            eventId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(eventId = eventId) as T
            }
        }
    }
}
