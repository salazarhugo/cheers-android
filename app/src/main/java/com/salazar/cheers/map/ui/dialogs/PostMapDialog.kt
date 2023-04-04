package com.salazar.cheers.map.ui.dialogs

import androidx.compose.runtime.Composable
import com.salazar.cheers.map.screens.map.MapUiState
import com.salazar.cheers.post.ui.item.PostItem
import com.salazar.cheers.ui.main.home.HomeUIAction


@Composable
fun PostMapDialog(
    uiState: MapUiState,
    onHomeUIAction: (HomeUIAction) -> Unit,
) {
    if (uiState.selectedPost != null)
        PostItem(
            post = uiState.selectedPost,
            onHomeUIAction = onHomeUIAction,
        )
}