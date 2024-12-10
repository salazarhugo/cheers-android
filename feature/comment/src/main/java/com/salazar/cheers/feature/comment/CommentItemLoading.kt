package com.salazar.cheers.feature.comment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.UserItemLoading

@Composable
fun CommentItemLoading(
    modifier: Modifier = Modifier,
) {
    UserItemLoading(
        modifier = modifier,
    )
}

@ComponentPreviews
@Composable
private fun CommentItemLoadingPreview() {
    CheersPreview {
        CommentItemLoading()
    }
}

