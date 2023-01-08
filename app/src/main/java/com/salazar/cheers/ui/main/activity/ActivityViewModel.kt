package com.salazar.cheers.ui.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.activity.ActivityRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActivityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activities: List<Activity>? = null,
    val friendRequestCounter: Int? = null,
    val friendRequestPicture: String? = null,
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ActivityUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        onSwipeRefresh()
    }

    private fun getFriendRequests() {
        viewModelScope.launch {
            friendshipRepository.listFriendRequest().collect { result ->
                when(result) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> viewModelState.update {
                        it.copy(
                            friendRequestCounter = result.data?.size,
                            friendRequestPicture = result.data?.firstOrNull()?.picture
                        )
                    }
                }
            }
        }
    }

    fun getActivity() {
        viewModelScope.launch {
            activityRepository.listActivity().collect { result ->
                when (result) {
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
            it.copy(errorMessage = errorMessage)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onSwipeRefresh() {
        getActivity()
        getFriendRequests()
    }
}

sealed class ActivityUIAction {
    object OnBackPressed : ActivityUIAction()
    object OnSwipeRefresh : ActivityUIAction()
    object OnFriendRequestsClick : ActivityUIAction()
    data class OnActivityClick(val activity: Activity) : ActivityUIAction()
}
