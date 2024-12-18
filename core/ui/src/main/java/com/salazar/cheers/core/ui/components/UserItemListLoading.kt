package com.salazar.cheers.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun UserItemListLoading(
    animationEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        repeat(20) {
            UserItemLoading(
                animationEnabled = animationEnabled,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SearchLoadingScreenPreview() {
    CheersPreview {
        UserItemListLoading()
    }
}
