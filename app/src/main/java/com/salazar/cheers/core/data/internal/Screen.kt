package com.salazar.cheers.core.data.internal

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.salazar.cheers.R
import com.salazar.cheers.feature.chat.ui.screens.messages.messagesNavigationRoute
import com.salazar.cheers.feature.create_post.createPostNavigationRoute
import com.salazar.cheers.feature.home.navigation.home.homeNavigationRoute
import com.salazar.cheers.feature.map.navigation.mapNavigationRoute
import com.salazar.cheers.feature.parties.partiesNavigationRoute
import com.salazar.cheers.feature.profile.navigation.profileNavigationRoute
import com.salazar.cheers.feature.search.navigation.searchNavigationRoute

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    @StringRes val label: Int,
) {
    data object Home : Screen(
        route = homeNavigationRoute,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Default.Home,
        label = R.string.menu_home,
    )

    data object Search : Screen(
        route = searchNavigationRoute,
        icon = Icons.Outlined.Search,
        selectedIcon = Icons.Filled.Search,
        label = R.string.search,
    )

    data object Messages : Screen(
        route = messagesNavigationRoute,
        icon = Icons.Outlined.ChatBubbleOutline,
        selectedIcon = Icons.Filled.ChatBubble,
        label = R.string.chat,
    )

    data object CreatePost : Screen(
        route = createPostNavigationRoute,
        icon = Icons.Outlined.AddBox,
        selectedIcon = Icons.Outlined.AddBox,
        label = R.string.search,
    )

    data object Parties : Screen(
        route = partiesNavigationRoute,
        label = R.string.search,
        icon = Icons.Outlined.Event,
        selectedIcon = Icons.Outlined.Event,
    )

    data object Profile : Screen(
        route = profileNavigationRoute,
        icon = Icons.Outlined.PersonOutline,
        selectedIcon = Icons.Filled.Person,
        label = R.string.profile,
    )

    data object Map : Screen(
        route = mapNavigationRoute,
        icon = Icons.Outlined.Map,
        selectedIcon = Icons.Filled.Map,
        label = R.string.map,
    )
}