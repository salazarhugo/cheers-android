package com.salazar.cheers.feature.create_note

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data object CreateNoteScreen

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/note/create"

fun NavController.navigateToCreateNote(
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = CreateNoteScreen,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.createNoteScreen(
    navigateBack: () -> Unit,
) {
    composable<CreateNoteScreen>(
        deepLinks = listOf(
            navDeepLink<CreateNoteScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        popEnterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        },
        popExitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down)
        }
    ) {
        CreateNoteRoute(
            navigateBack = navigateBack,
        )
    }
}
