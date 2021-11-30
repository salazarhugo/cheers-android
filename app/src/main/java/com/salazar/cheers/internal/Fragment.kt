package com.salazar.cheers.internal

import androidx.compose.ui.graphics.vector.ImageVector

data class Fragment(
    val navigationId: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String? = null,
)
