package com.salazar.cheers.ui.compose.bottombar

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.salazar.cheers.R
import com.salazar.cheers.core.data.internal.Screen
import com.salazar.cheers.feature.home.home.Home

data class BottomNavigationRoute<T : Any>(
    val route: T,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val order: Int,
    @StringRes val label: Int,
)

val HomeBottomNavigationRoute = BottomNavigationRoute(
    route = Home,
    icon = Icons.Outlined.Home,
    selectedIcon = Icons.Default.Home,
    label = R.string.menu_home,
    order = 1,
)

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
