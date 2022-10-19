package com.salazar.cheers.compose

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.salazar.cheers.ui.theme.CheersTheme
import androidx.compose.ui.graphics.lerp

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
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
    val containerColor = MaterialTheme.colorScheme.surface
//    val backgroundColor = lerp(
//        containerColor.value,
//        containerColor.value,
//        FastOutLinearInEasing.transform(scrollBehavior?.state?.overlappedFraction ?: 0f)
//    )
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//        containerColor = Color.Transparent,
//        scrolledContainerColor = Color.Transparent
    )
    Box(
        modifier = Modifier.background(backgroundColor)
    ) {
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
}

@Preview
@Composable
fun CheersAppBarPreview() {
    CheersTheme {
        CheersAppBar(title = { Text("Preview!") })
    }
}

@Preview
@Composable
fun CheersAppBarPreviewDark() {
    CheersTheme(darkTheme = true) {
        CheersAppBar(title = { Text("Preview!") })
    }
}