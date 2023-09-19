package com.salazar.cheers.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.core.data.internal.ClearRippleTheme
import com.salazar.cheers.core.data.internal.Screen
import com.salazar.cheers.core.ui.ui.MainDestinations
import com.salazar.cheers.feature.home.navigation.homeNavigationRoute
import com.salazar.cheers.feature.map.navigation.mapNavigationRoute
import com.salazar.cheers.feature.parties.partiesNavigationRoute
import com.salazar.cheers.feature.profile.navigation.profileNavigationRoute
import com.salazar.cheers.feature.search.navigation.searchNavigationRoute

@Composable
fun CheersBottomBar(
    unreadChatCount: Int,
    picture: String,
    currentRoute: String,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        Screen(
            route = partiesNavigationRoute,
            icon = {
                Icon(
                    Icons.Outlined.Home,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            selectedIcon = {
                Icon(
                    Icons.Rounded.Home,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            label = stringResource(id = R.string.menu_home),
        ),
        Screen(
            route = searchNavigationRoute,
            icon = {
                Icon(
                    Icons.Outlined.Search,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            selectedIcon = {
                Icon(
                    Icons.Filled.Search,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
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
            route = homeNavigationRoute,
            icon = {
                BadgedBox(badge = {
                    if (unreadChatCount > 0)
                        Badge {
                            Text(
                                text = unreadChatCount.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                }
                ) {
                    Icon(
                        painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            selectedIcon = {
                BadgedBox(badge = {
                    if (unreadChatCount > 0)
                        Badge {
                            Text(
                                text = unreadChatCount.toString(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                }
                ) {
                    Icon(
                        painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            label = "Messages"
        ),
    )

    CompositionLocalProvider(
        LocalRippleTheme provides ClearRippleTheme
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background.compositeOver(Color.White),
            modifier = Modifier
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
            NavigationBarItem(
                icon = {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = picture)
                                .apply(block = fun ImageRequest.Builder.() {
                                    transformations(CircleCropTransformation())
                                    error(R.drawable.default_profile_picture)
                                }).build()
                        ),
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        contentDescription = null,
                    )
                },
                selected = currentRoute == profileNavigationRoute,
                onClick = { onNavigate(profileNavigationRoute) },
            )
        }
    }
}
