package com.salazar.cheers.ui.main.camera

import android.app.Application
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.core.data.internal.User
import com.salazar.cheers.workers.UploadStoryWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraUiState(
    val isLoading: Boolean,
    val flashMode: Int = ImageCapture.FLASH_MODE_AUTO,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val user: User? = null,
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)
    private val viewModelState = MutableStateFlow(CameraUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect(::updateUser)
        }
    }

    private fun updateUser(user: User) {
        viewModelState.update {
            it.copy(user = user)
        }
    }

    fun uploadStory() {
        val imageUri = viewModelState.value.imageUri ?: return

        val uploadWorkRequest =
            OneTimeWorkRequestBuilder<UploadStoryWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "PHOTO" to imageUri.toString(),
                        "STORY_TYPE" to "IMAGE",
                        "PRIVACY" to "FRIENDS",
                    )
                )
                .build()

        workManager.enqueueUniqueWork(
            "upload_story",
            ExistingWorkPolicy.REPLACE,
            uploadWorkRequest,
        )
    }

    fun setImageUri(imageUri: Uri?) {
        viewModelState.update {
            it.copy(imageUri = imageUri)
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
