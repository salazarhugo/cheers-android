package com.salazar.cheers.ui.main.camera

import android.app.Application
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.salazar.cheers.workers.UploadStoryWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CameraUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val imageUri: Uri? = null,
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    application: Application
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
    }

    fun uploadStory() {
        val imageUri = viewModelState.value.imageUri ?: return

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadStoryWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "PHOTOS" to arrayOf(imageUri.toString()),
                        "STORY_TYPE" to "IMAGE",
                        "PRIVACY" to "FRIENDS",
                    )
                )
            }
                .build()

        workManager.enqueue(uploadWorkRequest)
    }

    fun setImageUri(imageUri: Uri?) {
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
