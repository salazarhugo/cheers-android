package com.salazar.cheers.core.ui.components.searchbar

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun SearchBarComponent(
    modifier: Modifier,
    searchInput: String,
    onSearchInputChanged: (String) -> Unit,
) {
    TextField(
        value = searchInput,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search icon",
            )
        },
        modifier = modifier,
        onValueChange = onSearchInputChanged,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(
            onSearch = {},
        ),
        placeholder = {
            Text("Search")
        }
    )
}


@ScreenPreviews
@Composable
private fun SearchBarComponentPreview() {
    CheersPreview {
        SearchBarComponent(
            searchInput = "",
            modifier = Modifier,
            onSearchInputChanged = {},
        )
    }
}
