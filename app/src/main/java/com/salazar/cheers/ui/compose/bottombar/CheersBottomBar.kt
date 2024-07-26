package com.salazar.cheers.ui.compose.bottombar

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.salazar.cheers.core.data.internal.ClearRippleTheme
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun CheersBottomBar(
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {},
) {
    val items = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Search,
        BottomNavigationItem.CreatePost,
        BottomNavigationItem.Messages,
        BottomNavigationItem.Profile,
    )

    CompositionLocalProvider(
        LocalRippleTheme provides ClearRippleTheme
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background.compositeOver(Color.White),
            modifier = modifier
                .navigationBarsPadding()
                .height(52.dp),
            windowInsets = BottomAppBarDefaults.windowInsets,
            tonalElevation = 0.dp,
        ) {
            items.forEachIndexed { index, bottomNavigationItem ->
                val screen = bottomNavigationItem.screen
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true

                val icon = when(bottomNavigationItem.icon) {
                    null -> {
                        val icon = when (isSelected) {
                            true -> screen.selectedIcon
                            false -> screen.icon
                        }
                        {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    else -> bottomNavigationItem.icon
                }
                NavigationBarItem(
                    modifier = Modifier.zIndex(bottomNavigationItem.order.toFloat()),
                    icon = icon,
                    selected = isSelected,
                    onClick = { onNavigate(screen.route) },
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun CheersBottomBarPreview() {
    CheersPreview {
        CheersBottomBar(
            currentDestination = null,
            modifier = Modifier,
        )
    }
}
