package com.salazar.cheers.core.ui.components.playback

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.circular_progress.CircularProgressComponent
import com.salazar.cheers.core.util.playback.AudioState


@Composable
fun PlaybackComponent(
    amplitudes: List<Int>,
    audioState: AudioState?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    PlaybackComponent(
        amplitudes = amplitudes,
        modifier = modifier,
        isPlaying = audioState?.isAudioPlaying ?: false,
        isLoading = audioState?.isLoading ?: false,
        progress = audioState?.audioProgress ?: 0f,
        onClick = onClick,
    )
}

@Composable
fun PlaybackComponent(
    amplitudes: List<Int>,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    isLoading: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit = {},
) {
    val icon = when(isPlaying) {
        true -> Icons.Rounded.Pause
        false -> Icons.Rounded.PlayArrow
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLoading) {
            CircularProgressComponent(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
            )
        } else {
            Icon(
                modifier = Modifier
                    .padding(8.dp),
                imageVector = icon,
                contentDescription = null,
            )
        }
        AudioWaveform(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                .clickable { onClick() },
            amplitudeType = AmplitudeType.Avg,
            amplitudes = amplitudes,
            progress = progress,
            progressBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            waveformBrush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
            onProgressChange = {
               onClick()
            },
            spikeWidth = 2.dp,
        )
    }
}

@ComponentPreviews
@Composable
private fun AudioPlayerComponentPreview() {
    PlaybackComponent(
        amplitudes = listOf(4, 5, 3, 7, 2, 3, 5, 2, 5, 3, 5, 6, 7, 8, 9),
        modifier = Modifier.padding(16.dp),
    )
}
