package com.salazar.cheers.internal

import androidx.compose.runtime.Composable

data class Screen(
    val route: String,
    val onNavigate: () -> Unit,
    val icon: @Composable () -> Unit,
    val selectedIcon: @Composable () -> Unit,
    val label: String? = null,
)