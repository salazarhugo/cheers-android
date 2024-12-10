package com.salazar.cheers.ui.main.party.create.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.SearchSuggestion
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.feature.search.ResultItem
import com.salazar.cheers.feature.search.SearchEmptyScreen
import com.salazar.cheers.shared.util.Resource

@Composable
fun CreatePartyLocationScreen(
    query: String,
    results: List<SearchSuggestion>,
    onQueryChange: (String) -> Unit,
    onLocationClick: (SearchSuggestion) -> Unit,
    onBackPressed: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    val onActiveChange = { a: Boolean -> expanded = a }
    val colors1 = SearchBarDefaults.colors()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true },
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.focusRequester(focusRequester),
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
//                        onSearch(it)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = onActiveChange,
                    enabled = true,
                    placeholder = {
                        Text(text = "Add location")
                    },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(
                                onClick = onBackPressed,
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back icon button",
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search icon",
                            )
                        }
                    },
                    trailingIcon = {
                        if (query.isEmpty()) return@InputField
                        IconButton(
                            onClick = {
                                onQueryChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear icon",
                            )
                        }
                    },
                )
            },
            expanded = expanded,
            onExpandedChange = onActiveChange,
            shape = SearchBarDefaults.inputFieldShape,
            colors = colors1,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            content = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.semantics { traversalIndex = 1f },
                ) {
                    searchResult(
                        query = query,
                        searchResult = Resource.Success(results),
                        onLocationClick = {
                            expanded = false
                            onLocationClick(it)
                        },
                    )
                }
            },
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics { traversalIndex = 1f },
        ) {
            searchResult(
                query = query,
                searchResult = Resource.Success(results),
                onLocationClick = onLocationClick,
            )
        }
    }
}

private fun LazyListScope.searchResult(
    query: String,
    searchResult: Resource<List<SearchSuggestion>>,
    onLocationClick: (SearchSuggestion) -> Unit,
) {
    when (searchResult) {
        is Resource.Error -> {
        }

        is Resource.Loading -> {
            item {
                LoadingScreen()
            }
        }

        is Resource.Success -> {
            val data = searchResult.data
            if (data.isNullOrEmpty()) {
                item {
                    SearchEmptyScreen(
                        query = query,
                    )
                }
                return
            }

            locations(
                locations = data,
                onLocationClick = onLocationClick,
            )
        }
    }
}

private fun LazyListScope.locations(
    locations: List<SearchSuggestion>,
    onLocationClick: (SearchSuggestion) -> Unit,
) {
    items(
        items = locations,
    ) { result ->
        ResultItem(
            name = result.name,
            icon = result.icon,
            address = result.address,
            onLocationClick = { onLocationClick(result) },
        )
    }
}

@ScreenPreviews
@Composable
private fun CreatePartyLocationScreenPreview() {
    CheersPreview {
        CreatePartyLocationScreen(
            query = "",
            onLocationClick = {},
            onQueryChange = {},
            results = emptyList(),
            onBackPressed = {},
        )
    }
}