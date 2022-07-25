package com.salazar.cheers.ui.main.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mapbox.search.result.SearchSuggestion

@Composable
fun SearchLocation(
    searchInput: String,
    results: List<SearchSuggestion>,
    onSearchInputChanged: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
) {
    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier.padding(16.dp),
                searchInput = searchInput,
                onSearchInputChanged = onSearchInputChanged,
            )
        },
    ) {
        SearchBody(
            results = results,
            modifier = Modifier.padding(it),
            onLocationClick = onLocationClick,
        )
    }
}

@Composable
private fun SearchBody(
    results: List<SearchSuggestion>,
    modifier: Modifier = Modifier,
    onLocationClick: (SearchSuggestion) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
//        item {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                CircularProgressIndicatorM3()
//            }
//        }
        items(results) { result ->
            ResultItem(
                name = result.name,
                icon = result.makiIcon,
                address = result.address?.formattedAddress(),
                onLocationClick = { onLocationClick(result) },
            )
        }
    }
}

@Composable
fun ResultItem(
    name: String?,
    icon: String?,
    address: String?,
    onLocationClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onLocationClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = icon,
            contentDescription = null,
        )
        Spacer(Modifier.width(16.dp))
        Column {
            if (name != null)
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                )
            if (address != null)
                Text(
                    text = address,
                    style = MaterialTheme.typography.labelMedium,
                )
        }
    }
}