package com.salazar.cheers.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun SearchEmptyScreen(
    query: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "No matches for `$query`",
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SearchEmptyScreenPreview() {
    CheersPreview {
        SearchEmptyScreen(cheersUserItem.username)
    }
}