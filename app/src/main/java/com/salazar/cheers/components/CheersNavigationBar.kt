package com.salazar.cheers.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Place
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
            MainDestinations.CAMERA_ROUTE,
            navigateToCamera,
            {
                Icon(
                    Icons.Default.FilterCenterFocus,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            {
                Icon(
                    Icons.Default.FilterCenterFocus,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            "Camera"
        ),
        Screen(
            MainDestinations.MESSAGES_ROUTE,
            navigateToMessages,
            {
                Icon(
                    painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon),
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            {
                Icon(
                    painter = rememberAsyncImagePainter(R.drawable.ic_bubble_icon),
                    null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
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
                        val unreadMessageCount = 0
                        if (index == 3 && unreadMessageCount > 0)
                            BadgedBox(badge = { Badge { Text(unreadMessageCount.toString()) } }) {
                                icon()
                            }
                        else
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
                                .data(data = profilePictureUrl).apply(block = fun ImageRequest.Builder.() {
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
