package com.salazar.cheers.feature.chat.ui.screens.mediapreview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MediaPreviewUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val mediaUri: String = "",
)

@HiltViewModel
class MediaPreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<MediaPreviewScreen>()

    private val viewModelState = MutableStateFlow(
        MediaPreviewUiState(
            mediaUri = args.mediaUri,
        )
    )

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {

    }
}

