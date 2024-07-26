package com.salazar.cheers.core.util.playback

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.salazar.cheers.core.util.audio.LocalAudio
import com.salazar.cheers.shared.di.MainDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class AndroidAudioPlayer @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @MainDispatcher
    private val dispatcher: CoroutineDispatcher,
): AudioPlayer {

    private var player: MediaPlayer? = null
    private val progressFlow = MutableStateFlow(0f)
    private val isLoadingFlow = MutableStateFlow(false)
    override val isPlayingFlow= MutableStateFlow(false)

    private lateinit var key: String

    private var positionUpdateJob: Job? = null

    override fun currentPositionFlow(): Flow<Float> {
        return progressFlow
    }

    override fun getAudioState(): Flow<AudioState> {
        return combine(progressFlow, isPlayingFlow, isLoadingFlow) { a, b, c ->
            AudioState(
                audioProgress = a,
                isAudioPlaying = b,
                isLoading = c,
            )
        }
    }

    private fun setupPlayerListeners() {
        player?.setOnBufferingUpdateListener { mediaPlayer, i ->
            if (i != 0) {
                isLoadingFlow.value = false
            }
        }
        player?.setOnCompletionListener {
            isPlayingFlow.value = false
            seekTo(0f)
        }
    }

    private fun startCoroutineTimer() {
        positionUpdateJob?.cancel()
        positionUpdateJob = CoroutineScope(dispatcher).launch {
            while (true) {
                delay(100) // Update position every 100ms
                val duration = player?.duration ?: return@launch
                val position = player?.currentPosition ?: return@launch
                progressFlow.value = position.toFloat() / duration
            }
        }
    }

    private fun start() {
        player?.start()
        isPlayingFlow.value = true
        startCoroutineTimer()
    }

    override suspend fun playFromUrl(url: String) {
        playLocalAudio(
            localAudio = LocalAudio(
                id = url,
                uri = Uri.parse(url),
                amplitudes = emptyList(),
                duration = 0L,
                name = "",
                path = "",
                size = 3L,
            )
        )
    }
    override suspend fun playLocalAudio(localAudio: LocalAudio) {
        val isNewAudio = ::key.isInitialized && localAudio.id != key
        if (isNewAudio) {
            stop()
        }

        if (player?.isPlaying == true) {
            pause()
        } else if (player?.isPlaying == false) {
            start()
        } else {
            isLoadingFlow.value = true
            try {
                val mp = MediaPlayer.create(
                    context,
                    localAudio.uri,
                )
                player = mp
                key = localAudio.id
                setupPlayerListeners()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
                isLoadingFlow.value = false
            }
        }
    }

    override fun seekTo(progress: Float) {
        val duration = player?.duration ?: return
        val msec = progress * duration
        player?.seekTo(msec.toInt())
        this.progressFlow.value = progress
    }

    override fun pause() {
        player?.pause()
        isPlayingFlow.value = false
        positionUpdateJob?.cancel()
    }

    override fun stop() {
        progressFlow.value = 0f
        isPlayingFlow.value = false
        try {
            player?.stop()
            player?.release()
            player = null
            positionUpdateJob?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}