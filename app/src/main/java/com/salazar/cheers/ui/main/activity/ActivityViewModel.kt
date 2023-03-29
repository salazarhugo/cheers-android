package com.salazar.cheers.ui.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.activity.ActivityRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.domain.usecase.cancel_friend_request.CancelFriendRequestUseCase
import com.salazar.cheers.domain.usecase.send_friend_request.SendFriendRequestUseCase
import com.salazar.cheers.friendship.domain.usecase.ListFriendRequestUseCase
import com.salazar.cheers.internal.Activity
import com.salazar.cheers.internal.Post
import com.salazar.cheers.user.domain.usecase.list_suggestions.ListSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
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
    val suggestions: List<UserItem>? = null,
    val friendRequestCounter: Int? = null,
    val friendRequestPicture: String? = null,
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
    private val activityRepository: ActivityRepository,
    private val listFriendRequestUseCase: ListFriendRequestUseCase,
    private val listSuggestionsUseCase: ListSuggestionsUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
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
        viewModelScope.launch {
            listFriendRequestUseCase().collect { requests ->
                viewModelState.update {
                    it.copy(
                        friendRequestCounter = requests.size,
                        friendRequestPicture = requests.firstOrNull()?.picture
                    )
                }
            }
        }
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
            listSuggestionsUseCase().onSuccess {
                updateSuggestions(it)
            }
        }
    }

    private fun getFriendRequests() {
        viewModelScope.launch {
            friendshipRepository.fetchFriendRequest().collect {}
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
            activityRepository.acknowledgeAll()
        }
    }

    private fun updateSuggestions(suggestions: List<UserItem>) {
        viewModelState.update {
            it.copy(suggestions = suggestions)
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
        refreshSuggestions()
    }

    fun onRemoveSuggestion(user: UserItem) {
        viewModelState.update {
            val l = it.suggestions?.toMutableList()
            l?.remove(user)
            it.copy(suggestions = l)
        }
    }

    fun onCancelFriendRequestClick(userID: String) {
        viewModelScope.launch {
            cancelFriendRequestUseCase(userId = userID).onSuccess {
            }
        }
    }

    fun onAddFriendClick(userID: String) {
        viewModelScope.launch {
            sendFriendRequestUseCase(userId = userID).onSuccess {
            }
        }
    }
}

sealed class ActivityUIAction {
    object OnBackPressed : ActivityUIAction()
    object OnSwipeRefresh : ActivityUIAction()
    object OnFriendRequestsClick : ActivityUIAction()
    data class OnActivityClick(val activity: Activity) : ActivityUIAction()
    data class OnUserClick(val userId: String) : ActivityUIAction()
    data class OnPostClick(val postId: String) : ActivityUIAction()
    data class OnCancelFriendRequestClick(val userID: String) : ActivityUIAction()
    data class OnAddFriendClick(val userID: String) : ActivityUIAction()
    data class OnRemoveSuggestion(val user: UserItem) : ActivityUIAction()
}
