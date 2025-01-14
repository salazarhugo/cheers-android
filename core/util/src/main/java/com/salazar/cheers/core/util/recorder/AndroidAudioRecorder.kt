package com.salazar.cheers.core.util.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.Job
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(
    private val context: Context
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var job: Job? = null

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(outputFile: File): Boolean {
        return try {
            createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(outputFile).fd)
                setAudioEncodingBitRate(192000)
                prepare()
                start()
                recorder = this
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun stop(): Boolean {
        return try {
            recorder?.stop()
            recorder?.reset()
            recorder = null
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}