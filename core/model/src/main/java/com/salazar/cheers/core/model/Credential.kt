package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class Credential(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val deviceName: String = "",
    val lastUsed: Long = 0L,
)