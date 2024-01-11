package com.salazar.cheers.feature.post_likes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val POST_ID = "postID"
const val postLikesNavigationRoute = "post_likes_route/{$POST_ID}"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/post/{${POST_ID}/likes"

fun NavController.navigateToPostLikes(
    postID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate("post_likes_route/$postID", navOptions)
}

fun NavGraphBuilder.postLikesScreen(
    navigateBack: () -> Unit,
    navigateToProfile: (String) -> Unit,
) {
    composable(
        route = postLikesNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        PostLikesRoute(
            onBackPressed = navigateBack,
            navigateToUser = navigateToProfile,
        )
    }
}
