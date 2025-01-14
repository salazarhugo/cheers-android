package com.salazar.cheers.feature.chat.ui.screens.mediapreview

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data class MediaPreviewScreen(
    val mediaUri: String,
)

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/media/{mediaUri}"

fun NavController.navigateToMediaPreview(
    mediaUri: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = MediaPreviewScreen(
            mediaUri = mediaUri,
        ),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.mediaPreviewScreen(
    navigateBack: () -> Unit,
) {
    composable<MediaPreviewScreen>(
        deepLinks = listOf(
            navDeepLink<MediaPreviewScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        }
    ) {
        MediaPreviewRoute(
            navigateBack = navigateBack,
        )
    }
}
