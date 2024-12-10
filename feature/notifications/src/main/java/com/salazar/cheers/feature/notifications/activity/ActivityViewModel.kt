package com.salazar.cheers.feature.notifications.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Activity
import com.salazar.cheers.core.model.ActivityType
import com.salazar.cheers.core.model.Filter
import com.salazar.cheers.core.model.INFO_FILTER_ID
import com.salazar.cheers.core.model.allFilter
import com.salazar.cheers.core.model.infoFilter
import com.salazar.cheers.data.activity.ActivityRepository
import com.salazar.cheers.domain.cancel_friend_request.CancelFriendRequestUseCase
import com.salazar.cheers.domain.list_friend_request.ListFriendRequestUseCase
import com.salazar.cheers.domain.list_suggestions.ListSuggestionsUseCase
import com.salazar.cheers.domain.send_friend_request.SendFriendRequestUseCase
import com.salazar.cheers.shared.util.Resource
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
    val filters: List<Filter>? = null,
    val activities: List<Activity>? = null,
    val suggestions: List<com.salazar.cheers.core.model.UserItem>? = null,
    val friendRequestCounter: Int? = null,
    val friendRequestPicture: String? = null,
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
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
        val filters = listOf(allFilter, infoFilter)
        viewModelState.update {
            it.copy(filters = filters)
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

    private fun updateSuggestions(suggestions: List<com.salazar.cheers.core.model.UserItem>) {
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

    fun onRemoveSuggestion(user: com.salazar.cheers.core.model.UserItem) {
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

    private fun updateFilter(filter: Filter) {
        viewModelState.update {
            val index = it.filters?.indexOfFirst { it.id == filter.id } ?: return
            it.copy(filters = it.filters.updated(index, filter))
        }
    }


    fun onFilterClick(filter: Filter) {
        val activityType = when (filter.id) {
            INFO_FILTER_ID -> ActivityType.INFORMATION
            else -> null
        }
        updateFilter(filter = filter.copy(selected = filter.selected.not()))

        viewModelState.update {
            it.copy(
                activities = it.activities?.filter {
                    it.type == activityType || !filter.selected
                }
            )
        }
    }
}

fun <E> Iterable<E>.updated(index: Int, elem: E): List<E> {
    return mapIndexed { i, existing -> if (i == index) elem else existing }
}

sealed class ActivityUIAction {
    data object OnBackPressed : ActivityUIAction()
    data object OnSwipeRefresh : ActivityUIAction()
    data object OnFriendRequestsClick : ActivityUIAction()
    data class OnActivityClick(val activity: Activity) : ActivityUIAction()
    data class OnUserClick(val userId: String) : ActivityUIAction()
    data class OnPostClick(val postId: String) : ActivityUIAction()
    data class OnCancelFriendRequestClick(val userID: String) : ActivityUIAction()
    data class OnAddFriendClick(val userID: String) : ActivityUIAction()
    data class OnRemoveSuggestion(val user: com.salazar.cheers.core.model.UserItem) :
        ActivityUIAction()
}
