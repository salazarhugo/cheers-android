package com.salazar.cheers.ui.camera

import android.net.Uri
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.extension.style.expressions.dsl.generated.image
import com.salazar.cheers.ui.event.PrivacyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CameraUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
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

}
