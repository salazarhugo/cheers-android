package com.salazar.cheers.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Fragment(
    val navigationId: Int,
    val icon: @Composable () -> Unit,
    val selectedIcon: @Composable () -> Unit,
    val label: String? = null,
)
