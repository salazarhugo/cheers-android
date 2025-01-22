package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    users: List<String> = emptyList(),
    isPrimary: Boolean = false,
    onSelectedChanged: (String) -> Unit = {},
) {
    if (users.isEmpty())
        return

    Column(modifier = modifier.padding(8.dp)) {
        LazyRow(
        ) {
            items(users) {
                SuggestionChip(
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 4.dp),
                    onClick = {
                        onSelectedChanged(it)
                    },
                    shape = MaterialTheme.shapes.medium,
                    label = {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Chip(
    name: String = "Chip",
    isSelected: Boolean = false,
    onSelectionChanged: (String) -> Unit = {},
    isPrimary: Boolean = false,
    unselectedColor: Color = MaterialTheme.colorScheme.primary,
) {
    val backgroundColor =
        if (isPrimary) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val textColor =
        if (isPrimary) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer

    Surface(
        modifier = Modifier.padding(4.dp),
        shadowElevation = 0.dp,
        shape = CircleShape,
        color = backgroundColor
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = {
                    onSelectionChanged(name)
                }
            )
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
    }
}