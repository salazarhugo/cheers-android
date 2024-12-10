package com.salazar.cheers.core.data.internal

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.RippleConfiguration

val clearRippleConfiguration = RippleConfiguration(
    rippleAlpha = RippleAlpha(
        draggedAlpha = 0.0f,
        focusedAlpha = 0.0f,
        hoveredAlpha = 0.0f,
        pressedAlpha = 0.0f,
    )
)