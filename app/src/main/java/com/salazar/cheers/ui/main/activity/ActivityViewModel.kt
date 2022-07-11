package com.salazar.cheers.ui.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Activity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class ActivityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val activities: List<Activity>? = null,
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ActivityUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        getActivity()
    }

    fun getActivity() {
        viewModelScope.launch {
           userRepository.getActivity(fetchFromRemote = true).collect { result ->
               when(result) {
                   is Resource.Loading -> updateIsLoading(result.isLoading)
                   is Resource.Error -> updateMessage(result.message)
                   is Resource.Success -> updateActivities(result.data)
               }
           }
        }
    }

    private fun updateActivities(activities: List<Activity>?) {
        viewModelState.update {
            it.copy(activities = activities)
        }
    }

    private fun updateMessage(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage ?: "")
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onSwipeRefresh() {
        getActivity()
    }
}

