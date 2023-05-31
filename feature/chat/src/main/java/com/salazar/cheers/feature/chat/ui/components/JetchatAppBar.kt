@file:OptIn(ExperimentalMaterial3Api::class)

package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CheersAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    center: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
) {
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
    )
    if (center)
        CenterAlignedTopAppBar(
            modifier = modifier,
            actions = actions,
            title = title,
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = navigationIcon,
        )
    else
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = foregroundColors,
            scrollBehavior = scrollBehavior
        )
}