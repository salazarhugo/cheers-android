package com.salazar.cheers.core.util.recorder

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}