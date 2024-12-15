package com.salazar.cheers.ui.main.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.domain.list_friend.ListMyFriendsUseCase
import com.salazar.cheers.parties.domain.usecase.get_party.GetPartyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ShareUIAction {
    object OnBackPressed : ShareUIAction()
    data class OnMessageChange(val message: String) : ShareUIAction()
}


data class ShareUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val message: String = "",
    val users: List<com.salazar.cheers.core.model.UserItem> = emptyList(),
    val party: Party? = null,
)

@HiltViewModel
class ShareViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val listFriendUseCase: ListMyFriendsUseCase,
    private val getPartyUseCase: GetPartyUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ShareUiState(isLoading = true))
    lateinit var partyId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("partyId")?.let {
            partyId = it
            viewModelScope.launch {
                getPartyUseCase(partyId = partyId).collect(::updateParty)
            }
        }

        viewModelScope.launch {
            //listFriendUseCase().collect(::updateUsers)
        }
    }

    fun onMessageChange(message: String) {
        viewModelState.update {
            it.copy(message = message)
        }
    }

    private fun updateParty(party: Party) {
        viewModelState.update {
            it.copy(party = party)
        }
    }

    private fun updateUsers(users: List<com.salazar.cheers.core.model.UserItem>) {
        viewModelState.update {
            it.copy(users = users)
        }
    }

    private fun updateError(message: String?) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }
}

