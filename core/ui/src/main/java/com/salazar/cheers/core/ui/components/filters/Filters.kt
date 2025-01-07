package com.salazar.cheers.core.ui.components.filters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Filter

@Composable
fun Filters(
    filters: List<Filter>,
    modifier: Modifier = Modifier,
    onFilterClick: (Filter) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        items(
            items = filters,
        ) { filter ->
            val selected = filter.selected

            FilterChip(
                modifier = Modifier
                    .animateItem(),
                selected = selected,
                onClick = {
                    onFilterClick(filter.copy(selected = !selected))
                },
                shape = MaterialTheme.shapes.medium,
                label = { Text(text = filter.name) },
                leadingIcon = {
                    AnimatedVisibility(
                        visible = selected,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally(),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                },
            )
        }
    }
}

