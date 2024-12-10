package com.salazar.cheers.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.UserItemLoading

@Composable
fun SearchLoadingScreen(
) {
    Column {
        repeat(20) {
            UserItemLoading()
        }
    }
}

@ScreenPreviews
@Composable
private fun SearchLoadingScreenPreview() {
    CheersPreview {
        SearchLoadingScreen()
    }
}