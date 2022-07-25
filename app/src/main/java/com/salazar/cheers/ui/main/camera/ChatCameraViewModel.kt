package com.salazar.cheers.ui.main.camera

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.internal.ChatChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatCameraUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val flashMode: Int = ImageCapture.FLASH_MODE_AUTO,
    val imageUri: Uri? = null,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val isImageTaken: Boolean = false,
    val room: ChatChannel? = null,
)

@HiltViewModel
class ChatCameraViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ChatCameraUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    lateinit var roomId: String

    init {
        stateHandle.get<String>("roomId")?.let {
            roomId = it
        }
        viewModelScope.launch {
            chatRepository.getChannel(channelId = roomId).collect { room ->
                viewModelState.update {
                    it.copy(room = room)
                }
            }
        }
    }

    fun sendImage() {
        val uri = uiState.value.imageUri ?: return

        viewModelScope.launch {
            chatRepository.sendImage(
                channelId = roomId,
                images = listOf(uri),
            )
        }
    }

    fun setImageUri(imageUri: Uri?) {
        viewModelState.update {
            it.copy(imageUri = imageUri)
        }
    }

    fun onCameraClick() {
        viewModelState.update {
            it.copy(isImageTaken = true)
        }
    }

    fun onSwitchFlash() {
        val nextFlashMode = when (viewModelState.value.flashMode) {
            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
            else -> ImageCapture.FLASH_MODE_AUTO
        }

        viewModelState.update {
            it.copy(flashMode = nextFlashMode)
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

