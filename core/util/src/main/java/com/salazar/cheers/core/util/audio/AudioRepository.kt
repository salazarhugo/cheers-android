package com.salazar.cheers.core.util.audio

import android.content.Context
import androidx.core.content.FileProvider
import com.salazar.common.di.IODispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val audioManager: AudioManager,
    @ApplicationContext
    private val context: Context,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
) {
     suspend fun loadAudioWithFile(file: File): LocalAudio = withContext(ioDispatcher) {
         val uri = FileProvider.getUriForFile(
             context,
             context.packageName + ".fileprovider",
            file,
         )
         val amplitudes = getAudioAmplitudes(file)
         return@withContext LocalAudio(
             id = UUID.randomUUID().toString(),
             name = file.name,
             amplitudes = amplitudes,
             size = file.totalSpace,
             uri = uri,
             path = file.path,
             duration = file.length(),
         )
    }

    private suspend fun getAudioAmplitudes(
        file: File,
    ): List<Int> = withContext(ioDispatcher) {
        return@withContext audioManager.getAmplitudesFromFile(file = file)
    }

}