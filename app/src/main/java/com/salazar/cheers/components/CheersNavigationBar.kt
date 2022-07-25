package com.salazar.cheers.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.internal.ClearRippleTheme
import com.salazar.cheers.internal.Screen
import com.salazar.cheers.navigation.MainDestinations

@Composable
fun CheersNavigationBar(
    unreadChatCount: Int,
    profilePictureUrl: String,
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToMap: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToMessages: () -> Unit,
    navigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen(
            MainDestinations.HOME_ROUTE,
            navigateToHome,
            { Icon(Icons.Outlined.Home, null, tint = MaterialTheme.colorScheme.onBackground) },
            { Icon(Icons.Rounded.Home, null, tint = MaterialTheme.colorScheme.onBackground) },
            "Home"
        ),
        Screen(
            MainDestinations.MAP_ROUTE,
            navigateToMap,
            { Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.onBackground) },
            { Icon(Icons.Filled.Place, null, tint = MaterialTheme.colorScheme.onBackground) },
            "Map"
        ),
        Screen(
            MainDestinations.EVENTS_ROUTE,
            navigateToCamera,
            { Icon(Icons.Outlined.Event, null, tint = MaterialTheme.colorScheme.onBackground) },
            { Icon(Icons.Default.Event, null, tint = MaterialTheme.colorScheme.onBackground) },
            "Camera"
        ),
        Screen(
            MainDestinations.MESSAGES_ROUTE,
            navigateToMessages,
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
            "Messages"
        ),
    )

    CompositionLocalProvider(
        LocalRippleTheme provides ClearRippleTheme
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background.compositeOver(Color.White),
            modifier = Modifier.height(52.dp),
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
                    onClick = screen.onNavigate,
                )
            }
            NavigationBarItem(
                icon = {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = profilePictureUrl)
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
                selected = currentRoute == MainDestinations.PROFILE_ROUTE,
                onClick = navigateToProfile,
            )
        }
    }
}
