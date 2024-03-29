package com.salazar.cheers.core.util.playback
data class AudioState(
    val isAudioPlaying: Boolean = false,
    val audioProgress: Float = 0f,
    val isLoading: Boolean = false,
)
