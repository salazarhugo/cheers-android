package com.salazar.cheers.ui.camera

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CameraUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
)

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(CameraUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun setImageUri(imageUri: Uri) {
        viewModelState.update {
            it.copy(imageUri = imageUri)
        }
    }

    fun onSwitchCameraClicked() {
        val lensFacing =
            if (viewModelState.value.lensFacing == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK

        viewModelState.update {
            it.copy(lensFacing = lensFacing)
        }
    }
}
