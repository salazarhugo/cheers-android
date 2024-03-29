package com.salazar.cheers.feature.map.ui.dialogs

import androidx.compose.runtime.Composable
import com.salazar.cheers.core.ui.components.post.PostComponent
import com.salazar.cheers.data.post.repository.Post
import com.salazar.cheers.feature.map.screens.map.MapUiState


@Composable
fun PostMapDialog(
    post: Post,
) {
    PostComponent(
        post = post,
    )
}