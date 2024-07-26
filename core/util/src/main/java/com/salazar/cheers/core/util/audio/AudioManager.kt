package com.salazar.cheers.core.util.audio

import com.salazar.cheers.shared.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import linc.com.amplituda.callback.AmplitudaErrorListener
import java.io.File
import javax.inject.Inject

class AudioManager @Inject constructor(
    private val amplituda: Amplituda,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getAmplitudesFromUrl(url: String): List<Int> = withContext(ioDispatcher) {
        return@withContext amplituda.processAudio(url, Cache.withParams(Cache.REUSE))
            .get(AmplitudaErrorListener {
                it.printStackTrace()
            })
            .amplitudesAsList()
    }

    suspend fun getAmplitudesFromFile(file: File): List<Int> = withContext(ioDispatcher) {
        return@withContext amplituda.processAudio(file)
            .get(AmplitudaErrorListener {
                it.printStackTrace()
            })
            .amplitudesAsList()
    }
}