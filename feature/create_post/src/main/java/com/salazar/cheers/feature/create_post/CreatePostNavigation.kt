package com.salazar.cheers.feature.create_post

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

const val PHOTO_URI = "photoUri"
const val createPostNavigationRoute = "create_post_route?$PHOTO_URI={$PHOTO_URI}"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/create_post"

fun NavController.navigateToCreatePost(
    photoUri: String? = null,
    navOptions: NavOptions? = null,
) {
    this.navigate("create_post_route?$PHOTO_URI=$photoUri", navOptions)
}

fun NavGraphBuilder.createPostScreen(
    navigateBack: () -> Unit,
    navigateToCamera: () -> Unit,
) {
    composable(
        route = createPostNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
        arguments = listOf(
            navArgument(PHOTO_URI) {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        CreatePostRoute(
            navigateBack = navigateBack,
            navigateToCamera = navigateToCamera,
        )
    }
}
