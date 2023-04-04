package com.salazar.cheers.ui.compose.suggestions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.core.data.internal.SuggestionUser


@Composable
fun Suggestions(suggestions: List<SuggestionUser>) {
    val pagerState = rememberPagerState()

    HorizontalPager(
        count = suggestions.size,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 76.dp),
    ) { page ->
        Suggestion(suggestions[page], this, page)
    }
}