package com.salazar.cheers.feature.create_post.audio_recorder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.util.audio.AudioRepository
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.core.util.playback.AndroidAudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


data class AudioRecorderUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val localAudio: LocalAudio? = null,
    val isAudioPlaying: Boolean = false,
    val audioProgress: Float = 0f,
)

@HiltViewModel
class AudioRecorderViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val audioPlayer: AndroidAudioPlayer,
    stateHandle: SavedStateHandle,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(AudioRecorderUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            audioPlayer.isPlayingFlow.collect(::updateIsAudioPlaying)
        }

        viewModelScope.launch {
            audioPlayer.currentPositionFlow().collect(::updateAudioProgress)
        }
    }

    private fun updateIsAudioPlaying(isAudioPlaying: Boolean) {
        viewModelState.update {
            it.copy(isAudioPlaying = isAudioPlaying)
        }
    }

    private fun updateAudioProgress(progress: Float) {
        viewModelState.update {
            it.copy(audioProgress = progress)
        }
    }
    fun updateErrorMessage(errorMessage: String) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    fun onStopRecording(file: File) {
        loadAudioWithFile(file = file)
    }

    private fun loadAudioWithFile(file: File) {
        viewModelScope.launch {
            val localAudio = audioRepository.loadAudioWithFile(file = file)
            viewModelState.update {
                it.copy(localAudio = localAudio)
            }
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onPlaybackClick() {
        val audio = uiState.value.localAudio ?: return
        viewModelScope.launch(Dispatchers.IO) {
            audioPlayer.playLocalAudio(audio)
        }
    }

    fun onResetClick() {
        viewModelState.update {
            it.copy(localAudio = null)
        }
        audioPlayer.stop()
    }
}

sealed class AudioRecorderUIAction {
    data object OnBackPressed : AudioRecorderUIAction()
    data class OnStartRecording(val audioFile: File) : AudioRecorderUIAction()
    data object OnStopRecording : AudioRecorderUIAction()
    data object OnStartPlaying : AudioRecorderUIAction()
    data object OnStopPlaying : AudioRecorderUIAction()
    data class OnNotificationChange(val enabled: Boolean) : AudioRecorderUIAction()
}
