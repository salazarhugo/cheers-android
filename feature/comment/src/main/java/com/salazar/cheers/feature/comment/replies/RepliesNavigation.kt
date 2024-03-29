package com.salazar.cheers.feature.comment.replies

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

internal const val COMMENT_ID = "commentID"
const val repliesNavigationRoute = "comment_replies_route/{$COMMENT_ID}"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/comments/{${COMMENT_ID}/replies"

fun NavController.navigateToReplies(
    commentID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate("comment_replies_route/$commentID", navOptions)
}

fun NavGraphBuilder.repliesScreen(
    navigateBack: () -> Unit,
    navigateToCommentMoreSheet: (String) -> Unit,
) {
    composable(
        route = repliesNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        RepliesRoute(
            navigateToCommentMoreSheet = navigateToCommentMoreSheet,
            navigateBack = navigateBack,
        )
    }
}
