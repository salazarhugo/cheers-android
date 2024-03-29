package com.salazar.cheers.ui.compose

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.Map
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R
import com.salazar.cheers.core.data.internal.ClearRippleTheme
import com.salazar.cheers.core.data.internal.Screen
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.feature.home.navigation.home.homeNavigationRoute
import com.salazar.cheers.feature.map.navigation.mapNavigationRoute
import com.salazar.cheers.feature.parties.partiesNavigationRoute
import com.salazar.cheers.feature.profile.navigation.profileNavigationRoute
import com.salazar.cheers.feature.search.navigation.searchNavigationRoute

@Composable
fun CheersBottomBar(
    currentRoute: String,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {},
) {
    val items = listOf(
        Screen(
            route = homeNavigationRoute,
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            selectedIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            label = stringResource(id = R.string.menu_home),
        ),
        Screen(
            route = searchNavigationRoute,
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            },
            selectedIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            },
            label = stringResource(id = R.string.search),
        ),
//        Screen(
//            route = MainDestinations.EVENTS_ROUTE,
//            icon = { Icon(Icons.Outlined.Bolt, null, tint = MaterialTheme.colorScheme.onBackground) },
//            selectedIcon = { Icon(Icons.Filled.Bolt, null, tint = MaterialTheme.colorScheme.onBackground) },
//            label = "Events"
//        ),
        Screen(
            route = mapNavigationRoute,
            icon = {
                Icon(
                    Icons.Outlined.Map,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            selectedIcon = {
                Icon(
                    Icons.Default.Map,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            label = stringResource(id = R.string.map)
        ),
//        Screen(
//            route = MainDestinations.TICKETS_ROUTE,
//            icon = { Icon(Icons.Outlined.ConfirmationNumber, null, tint = MaterialTheme.colorScheme.onBackground) },
//            selectedIcon = { Icon(Icons.Default.ConfirmationNumber, null, tint = MaterialTheme.colorScheme.onBackground) },
//            label = "Tickets"
//        ),
        Screen(
            route = partiesNavigationRoute,
            icon = {
                Icon(
                    imageVector = Icons.Default.PeopleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            selectedIcon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            label = "Friends"
        ),
        Screen(
            route = profileNavigationRoute,
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            selectedIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            label = "Profile"
        ),
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
            items.forEachIndexed { index, screen ->
                NavigationBarItem(
                    icon = {
                        val icon =
                            if (currentRoute == screen.route) screen.selectedIcon else screen.icon
                        icon()
                    },
                    selected = currentRoute == screen.route,
                    onClick = {onNavigate(screen.route)},
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
            currentRoute = "",
            modifier = Modifier,
        )
    }
}
