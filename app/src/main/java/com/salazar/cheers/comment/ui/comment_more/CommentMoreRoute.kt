package com.salazar.cheers.comment.ui.comment_more

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.core.share.ui.CheersNavigationActions
import com.salazar.cheers.core.share.ui.LoadingScreen

/**
 * Stateful composable that displays the Navigation route for the Comments screen.
 *
 * @param commentMoreViewModel that handles the business logic of this screen
 */
@Composable
fun CommentMoreRoute(
    commentMoreViewModel: CommentMoreViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by commentMoreViewModel.uiState.collectAsStateWithLifecycle()

    val comment = uiState.comment
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    if (comment == null)
        com.salazar.cheers.core.share.ui.LoadingScreen()
    else
        CommentMoreSheet(
            modifier = Modifier.navigationBarsPadding(),
            isAuthor = comment.authorId == uid,
            onDelete = {
                navActions.navigateToDeleteCommentDialog(comment.id)
            },
            onReport = {},
        )
}