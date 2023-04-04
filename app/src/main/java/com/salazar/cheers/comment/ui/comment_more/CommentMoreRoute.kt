package com.salazar.cheers.comment.ui.comment_more

import android.content.Intent
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.core.data.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.core.data.util.Utils.copyToClipboard

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
    val uiState by commentMoreViewModel.uiState.collectAsState()

    val comment = uiState.comment
    val uid = FirebaseAuth.getInstance().currentUser?.uid

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