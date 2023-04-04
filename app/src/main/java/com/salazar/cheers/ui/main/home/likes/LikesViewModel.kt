package com.salazar.cheers.ui.main.home.likes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.data.internal.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LikesViewModel @AssistedInject constructor(
    @Assisted private val channelId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(LikesViewModelState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshPostLikes(channelId)
    }

    private fun refreshPostLikes(channelId: String) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
//            viewModelState.update {
//                val result = Neo4jUtil.getPostLikes(channelId)
//                when (result) {
//                    is Result.Success -> it.copy(users = result.data, isLoading = false)
//                    is Result.Error -> it.copy(
//                        isLoading = false,
//                        errorMessages = listOf(result.exception.toString())
//                    )
//                }
//            }
        }
    }

    @AssistedFactory
    interface LikesViewModelFactory {
        fun create(postId: String): LikesViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: LikesViewModelFactory,
            postId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(postId = postId) as T
            }
        }
    }
}

data class LikesViewModelState(
    val users: List<User>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val query: String = "",
)

