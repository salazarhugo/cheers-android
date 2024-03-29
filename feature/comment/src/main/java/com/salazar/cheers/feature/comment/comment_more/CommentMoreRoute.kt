package com.salazar.cheers.feature.comment.comment_more

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.core.ui.ui.LoadingScreen

@Composable
fun CommentMoreRoute(
    commentMoreViewModel: CommentMoreViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by commentMoreViewModel.uiState.collectAsStateWithLifecycle()

    val comment = uiState.comment
    val uid = ""

    if (comment == null)
        LoadingScreen()
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