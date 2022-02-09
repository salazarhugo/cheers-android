package com.salazar.cheers.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.animations.Animate

@Preview(showBackground = true)
@Composable
fun ChipGroup(
    users: List<String> = emptyList(),
    selectedCar: String? = null,
    onSelectedChanged: (String) -> Unit = {},
    selectedColor: Color = Color.LightGray,
    unselectedColor: Color = MaterialTheme.colorScheme.primary,
) {
    Column(modifier = Modifier.padding(8.dp)) {
        LazyRow {
            items(users) {
                Animate {
                    Chip(
                        name = it,
//                    isSelected = selectedCar == it,
                        onSelectionChanged = {
                            onSelectedChanged(it)
                        },
                        selectedColor = selectedColor,
                        unselectedColor = unselectedColor,
                    )
                }
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
    selectedColor: Color = Color.LightGray,
    unselectedColor: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(
        modifier = Modifier.padding(4.dp),
        shadowElevation = 8.dp,
        shape = CircleShape,
        color = if (isSelected) Color.LightGray else MaterialTheme.colorScheme.primary
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
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
    }
}