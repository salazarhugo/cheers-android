package com.salazar.cheers.core.data.internal

import androidx.compose.runtime.Composable

data class Screen(
    val route: String,
    val icon: @Composable () -> Unit,
    val selectedIcon: @Composable () -> Unit,
    val label: String? = null,
)