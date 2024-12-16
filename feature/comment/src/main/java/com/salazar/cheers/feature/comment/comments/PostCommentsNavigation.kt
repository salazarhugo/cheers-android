package com.salazar.cheers.feature.comment.comments

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

@Serializable
data class PostCommentsScreen(
    val postID: String = "",
)

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/post/{postID}/comments"

fun NavController.navigateToPostComments(
    postID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = PostCommentsScreen(postID = postID),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.postCommentsScreen(
    navigateBack: () -> Unit,
    navigateToCommentMoreSheet: (String) -> Unit,
    navigateToCommentReplies: (String) -> Unit,
    navigateToUser: (String) -> Unit,
) {
    composable<PostCommentsScreen>(
        deepLinks = listOf(
            navDeepLink<PostCommentsScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        CommentsRoute(
            onBackPressed = navigateBack,
            navigateToCommentMoreSheet = navigateToCommentMoreSheet,
            navigateToCommentReplies = navigateToCommentReplies,
            navigateToUser = navigateToUser,
        )
    }
}
