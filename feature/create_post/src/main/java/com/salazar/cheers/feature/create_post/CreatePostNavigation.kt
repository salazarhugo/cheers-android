package com.salazar.cheers.feature.create_post

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data class CreatePost(
    val photoUri: String? = null,
)

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/createPost"

fun NavController.navigateToCreatePost(
    photoUri: String? = null,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = CreatePost(photoUri = photoUri),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.createPostScreen(
    navigateBack: () -> Unit,
    navigateToCamera: () -> Unit,
) {
    composable<CreatePost>(
        deepLinks = listOf(
            navDeepLink<CreatePost>(basePath = DEEP_LINK_URI_PATTERN),
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
        CreatePostRoute(
            navigateBack = navigateBack,
            navigateToCamera = navigateToCamera,
        )
    }
}
