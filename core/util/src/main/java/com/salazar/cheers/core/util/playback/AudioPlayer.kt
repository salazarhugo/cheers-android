package com.salazar.cheers.core.util.playback

import android.net.Uri
import com.salazar.cheers.core.util.audio.LocalAudio
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AudioPlayer {

    val isPlayingFlow: Flow<Boolean>
    fun currentPositionFlow(): Flow<Float>

    fun getAudioState(): Flow<AudioState>
    suspend fun playLocalAudio(localAudio: LocalAudio)
    suspend fun playFromUrl(url: String)
    fun seekTo(progress: Float)
    fun stop()
    fun pause()
}