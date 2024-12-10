package com.salazar.cheers.ui.compose.bottombar

import androidx.compose.runtime.Composable
import com.salazar.cheers.core.data.internal.Screen

sealed class BottomNavigationItem(
    val screen: Screen,
    val icon: (@Composable () -> Unit)? = null,
    val order: Int = 0,
) {
    data object Home : BottomNavigationItem(
        screen = Screen.Home,
        order = 2,
    )

    data object Search : BottomNavigationItem(
        screen = Screen.Search,
        order = 1,
    )

    data object CreatePost : BottomNavigationItem(
        screen = Screen.CreatePost,
        icon = {
            CreateBottomNavigationIcon(isSelected = false)
        },
        order = 3,
    )

    data object Messages : BottomNavigationItem(
        screen = Screen.Messages,
        order = 3,
    )

    data object Profile : BottomNavigationItem(
        screen = Screen.Profile,
        order = 3,
    )
}
