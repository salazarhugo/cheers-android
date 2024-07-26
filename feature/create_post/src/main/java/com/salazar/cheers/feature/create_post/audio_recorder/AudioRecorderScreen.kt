package com.salazar.cheers.feature.create_post.audio_recorder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.playback.LargePlaybackComponent
import com.salazar.cheers.core.ui.components.record_button.RecordButtonComponent
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.core.util.recorder.AndroidAudioRecorder
import com.salazar.cheers.shared.util.LocalActivity
import java.io.File


@Composable
fun AudioRecorderScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioRecorderViewModel = hiltViewModel(),
    onAudioRecorderUIAction: (AudioRecorderUIAction) -> Unit = {},
    onDone: (LocalAudio) -> Unit = {},
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val activity = LocalActivity.current
    val audioRecorder = remember {
        AndroidAudioRecorder(activity.applicationContext)
    }
    val localAudio = uiState.localAudio
    var isRecording by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            IconButton(
                onClick = {
                    onAudioRecorderUIAction(AudioRecorderUIAction.OnBackPressed)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = null,
                )
            }
        },
        bottomBar = {
            val file = remember {
                File(activity.cacheDir, "audio.aac")
            }
            AudioRecorderBottomBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 64.dp),
                showPlaybackControls = isRecording.not() && localAudio != null,
                isPlaybackPlaying = uiState.isAudioPlaying,
                isRecording = isRecording,
                onRecordClick = {
                    if (isRecording) {
                        audioRecorder.stop()
                        viewModel.onStopRecording(file)
                    } else  {
                        audioRecorder.start(file)
                    }
                    isRecording = !isRecording
                },
                onDoneClick = {
                    if (localAudio != null) {
                        onDone(localAudio)
                    }
                },
                onResetClick = {
                    viewModel.onResetClick()
                },
                onPlaybackClick = {
                    viewModel.onPlaybackClick()
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            LargePlaybackComponent(
                modifier = Modifier.size(128.dp),
                amplitudes = listOf(0,0,0,0,0,0,0,0),
            )
            if (localAudio != null) {
                LargePlaybackComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 32.dp)
                    ,
                    amplitudes = localAudio.amplitudes,
                    isPlaying = uiState.isAudioPlaying,
                    progress = uiState.audioProgress,
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecording) {
                audioRecorder.stop()
            }
        }
    }
}

@Composable
fun AudioRecorderBottomBar(
    showPlaybackControls: Boolean,
    isPlaybackPlaying: Boolean,
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    onRecordClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
    onPlaybackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showPlaybackControls) {
            PlaybackControls(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = isPlaybackPlaying,
                onDoneClick = onDoneClick,
                onResetClick = onResetClick,
                onPlaybackClick = onPlaybackClick,
            )
        } else {
            RecordButtonComponent(
                isRecording = isRecording,
                onClick = onRecordClick,
            )
        }
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onResetClick: () -> Unit = {},
    onPlaybackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
) {
    val icon = when(isPlaying) {
        true -> Icons.Rounded.Pause
        false -> Icons.Rounded.PlayArrow
    }

    Row(
        modifier = modifier.padding(horizontal = 64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            modifier = Modifier
                .size(30.dp)
                .clickable { onResetClick() },
            imageVector = Icons.Default.RestartAlt,
            contentDescription = null,
        )
        Icon(
            modifier = Modifier
                .size(80.dp)
                .clickable { onPlaybackClick() },
            imageVector = icon,
            contentDescription = null,
        )
        TextButton(
            onClick = onDoneClick,
        ) {
            Text(
                text = "Done",
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun VoiceRecordingScreenPreview() {
    CheersPreview {
        AudioRecorderScreen(
            modifier = Modifier,
        )
    }
}