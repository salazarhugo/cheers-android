package com.salazar.cheers.core.util.audio

import android.net.Uri

data class LocalAudio(
    val id: String,
    val uri: Uri,
    val path: String,
    val name: String,
    val duration: Long,
    val size: Long,
    val amplitudes: List<Int> = emptyList(),
) {
    val nameWithoutExtension: String get() = name.substringBeforeLast('.')
}