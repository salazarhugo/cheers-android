package com.salazar.cheers.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.salazar.cheers.ui.theme.CheersTheme

@Composable
fun CheersAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val backgroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
    val backgroundColor = backgroundColors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    ).value
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    )
    Box(modifier = Modifier.background(backgroundColor)) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            actions = actions,
            title = title,
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = {
//                CheersIcon(
//                    contentDescription = stringResource(id = R.string.navigation_drawer_open),
//                    modifier = Modifier
//                        .size(64.dp)
//                        .clickable(onClick = onNavIconPressed)
//                        .padding(16.dp)
//                )
            }
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