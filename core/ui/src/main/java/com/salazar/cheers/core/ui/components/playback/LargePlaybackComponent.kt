package com.salazar.cheers.core.ui.components.playback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
fun LargePlaybackComponent(
    amplitudes: List<Int>,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit = {},
) {
    AudioWaveform(
        modifier = modifier.padding(vertical = 8.dp)
            .clickable { onClick() },
        amplitudes = amplitudes.take(11),
        amplitudeType = AmplitudeType.Avg,
        progress = progress,
        progressBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        waveformBrush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
        onProgressChange = {
           onClick()
        },
        spikeWidth = 8.dp,
        spikePadding = 8.dp,
    )
}

@ComponentPreviews
@Composable
private fun LargeAudioPlayerComponentPreview() {
    LargePlaybackComponent(
        amplitudes = listOf(4, 5, 3, 7, 2, 3, 5, 2, 5, 3, 5, 6, 7, 8, 9),
        modifier = Modifier.padding(16.dp),
    )
}
