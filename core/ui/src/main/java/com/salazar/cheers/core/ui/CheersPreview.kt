package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.ui.theme.CheersTheme


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheersPreview(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable RowScope.() -> Unit,
) {
    CheersTheme {
        Surface(
            content = {
              FlowRow(
                  modifier = modifier,
                  horizontalArrangement = horizontalArrangement,
                  verticalArrangement = verticalArrangement,
                  maxItemsInEachRow = maxItemsInEachRow,
                  content = content
              )
            },
        )
    }
}