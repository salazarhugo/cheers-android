package com.salazar.cheers.feature.comment.replies

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable

private const val DEEP_LINK_URI_PATTERN =
    "${Constants.DEEPLINK_BASE_URL}/comments/{commentID}/replies"

@Serializable
data class RepliesScreen(
    val commentID: String = "",
)

fun NavController.navigateToReplies(
    commentID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = RepliesScreen(commentID = commentID),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.repliesScreen(
    navigateBack: () -> Unit,
    navigateToCommentMoreSheet: (String) -> Unit,
    navigateToUser: (String) -> Unit,
) {
    composable<RepliesScreen>(
        deepLinks = listOf(
            navDeepLink<RepliesScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        RepliesRoute(
            navigateToCommentMoreSheet = navigateToCommentMoreSheet,
            navigateBack = navigateBack,
            navigateToUser = navigateToUser,
        )
    }
}
