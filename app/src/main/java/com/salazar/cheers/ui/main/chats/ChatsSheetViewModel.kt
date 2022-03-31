package com.salazar.cheers.ui.main.chats

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.repository.ChatRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatsSheetUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)

class ChatsSheetViewModel @AssistedInject constructor(
    private val chatRepository: ChatRepository,
    @Assisted private val channelId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ChatsSheetUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun deleteChannel() {
        viewModelScope.launch {
//            chatRepository.deleteChannel(channelId = channelId)
        }
    }

    @AssistedFactory
    interface ChatsSheetViewModelFactory {
        fun create(channelId: String): ChatsSheetViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: ChatsSheetViewModelFactory,
            channelId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(channelId = channelId) as T
            }
        }
    }
}

@Composable
fun chatsSheetViewModel(channelId: String): ChatsSheetViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).chatsSheetViewModelFactory()

    return viewModel(factory = ChatsSheetViewModel.provideFactory(factory, channelId = channelId))
}
