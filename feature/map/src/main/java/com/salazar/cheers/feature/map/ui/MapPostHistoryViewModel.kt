package com.salazar.cheers.feature.map.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.map.MapRepositoryImpl
import com.salazar.cheers.core.Post
import com.salazar.cheers.domain.list_map_post.ListMapPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapPostHistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val posts: List<Post>? = null,
)

@HiltViewModel
class MapPostHistoryViewModel @Inject constructor(
    val mapRepository: MapRepositoryImpl,
    private val listMapPostUseCase: ListMapPostUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MapPostHistoryUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            listMapPostUseCase().collect(::updatePosts)
        }
    }

    private fun updatePosts(mapPosts: List<Post>) {
        viewModelState.update {
            it.copy(posts = mapPosts, isLoading = false)
        }
    }

}

