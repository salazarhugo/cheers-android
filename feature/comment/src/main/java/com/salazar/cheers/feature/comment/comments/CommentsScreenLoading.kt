package com.salazar.cheers.feature.comment.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.feature.comment.CommentItemLoading

@Composable
fun CommentsScreenLoading() {
    Column {
        repeat(20) {
            CommentItemLoading()
        }
    }
}

@ScreenPreviews
@Composable
private fun CommentsScreenLoadingPreview() {
    CheersPreview {
        CommentsScreenLoading()
    }
}

