package com.salazar.cheers.ui.compose.bottombar

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.salazar.cheers.core.data.internal.clearRippleConfiguration
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CheersBottomBar(
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onNavigate: (Any) -> Unit = {},
) {
    var showAccountBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val viewConfiguration = LocalViewConfiguration.current

    val items = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Search,
        BottomNavigationItem.CreatePost,
        BottomNavigationItem.Messages,
        BottomNavigationItem.Profile,
    )

    if (showAccountBottomSheet) {
        AccountBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    showAccountBottomSheet = false
                }
            }
        )
    }

    CompositionLocalProvider(
        LocalRippleConfiguration provides clearRippleConfiguration
    ) {
        Column {
            HorizontalDivider(
                thickness = 0.5.dp,
            )
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background.compositeOver(Color.White),
                modifier = modifier
                    .navigationBarsPadding()
                    .height(52.dp),
                windowInsets = BottomAppBarDefaults.windowInsets,
                tonalElevation = 0.dp,
            ) {
                items.forEachIndexed { index, bottomNavigationItem ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val screen = bottomNavigationItem.screen
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(screen.route::class)
                    } == true


                    val icon = when (bottomNavigationItem.icon) {
                        null -> {
                            val icon = when (isSelected) {
                                true -> screen.selectedIcon
                                false -> screen.icon
                            }
                            {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = screen.route::class.simpleName,
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        else -> bottomNavigationItem.icon
                    }
                    LaunchedEffect(interactionSource) {
                        var isLongClick = false

                        interactionSource.interactions.collectLatest { interaction ->
                            when (interaction) {
                                is PressInteraction.Press -> {
                                    isLongClick = false
                                    delay(viewConfiguration.longPressTimeoutMillis)
                                    isLongClick = true
                                    hapticFeedback.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
                                    if (bottomNavigationItem == BottomNavigationItem.Profile) {
                                        showAccountBottomSheet = true
                                    }
                                }

                                is PressInteraction.Release -> {
                                    if (isLongClick.not()) {
                                        onNavigate(screen.route)
                                    }
                                }
                            }
                        }
                    }

                    NavigationBarItem(
                        modifier = Modifier
                            .zIndex(bottomNavigationItem.order.toFloat()),
                        icon = icon,
                        selected = isSelected,
                        onClick = {},
                        interactionSource = interactionSource,
                    )
                }
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
