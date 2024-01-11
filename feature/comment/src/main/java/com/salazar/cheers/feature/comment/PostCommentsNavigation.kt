package com.salazar.cheers.feature.comment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val POST_ID = "postID"
const val postCommentsNavigationRoute = "post_comments_route/{$POST_ID}"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/post/{${POST_ID}/comments"

fun NavController.navigateToPostComments(
    postID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate("post_comments_route/$postID", navOptions)
}

fun NavGraphBuilder.postCommentsScreen(
    navigateBack: () -> Unit,
    navigateToCommentMoreSheet: (String) -> Unit,
    navigateToCommentReplies: (String) -> Unit,
) {
    composable(
        route = postCommentsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        CommentsRoute(
            onBackPressed = navigateBack,
            navigateToCommentMoreSheet = navigateToCommentMoreSheet,
            navigateToCommentReplies = navigateToCommentReplies,
        )
    }
}
