package com.salazar.cheers.feature.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.RecentSearch
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.UserSuggestion
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.item.party.PartyItem
import com.salazar.cheers.core.ui.theme.Typography
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.util.Utils.conditional
import com.salazar.cheers.feature.search.components.RecentSearchComponent
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearchInputChanged: (String) -> Unit,
    onUserClick: (UserItem) -> Unit,
    onPartyClick: (partyID: String) -> Unit,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onSwipeRefresh: () -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onFollowToggle: (String) -> Unit,
    onMapClick: () -> Unit,
    onSearch: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val onActiveChange = { a: Boolean -> expanded = a }
    val colors1 = SearchBarDefaults.colors()
    val query = uiState.searchInput
    val searchResultState = uiState.searchResultState
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }
    val tabs = listOf(
        "Parties",
        "Users",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true },
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .conditional(
                    condition = !expanded,
                    modifier = {
                        padding(horizontal = 16.dp)
                    }
                )
                .fillMaxWidth()
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onSearchInputChanged,
                    onSearch = {
                        onSearch(it)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = onActiveChange,
                    enabled = true,
                    placeholder = {
                        Text(text = "Search people, parties or venues")
                    },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(
                                onClick = {
                                    expanded = false
                                },
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
                        if (query.isEmpty()) {
                            IconButton(
                                onClick = onMapClick,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Map,
                                    contentDescription = null,
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    onSearchInputChanged("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Clear icon",
                                )
                            }
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
            windowInsets = SearchBarDefaults.windowInsets,
            content = {
                SearchBody(
                    query = query,
                    recentSearches = uiState.recentSearch,
                    searchResultState = searchResultState,
                    onUserClick = onUserClick,
                    onDeleteRecentUser = onDeleteRecentUser,
                    onRecentSearchClick = onRecentSearchClick,
                    onFollowToggle = onFollowToggle,
                    onPartyClick = onPartyClick,
                )
            },
        )
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    text = { Text(text = tab) },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) { page ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { traversalIndex = 1f },
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                searchResult(
                    page = page,
                    query = query,
                    searchResultState = searchResultState,
                    onUserClick = onUserClick,
                    onPartyClick = onPartyClick,
                    onStoryClick = {},
                )
            }
        }
    }
}

@Composable
fun UsersTab(modifier: Modifier = Modifier) {

}

@Composable
fun PartiesTab(modifier: Modifier = Modifier) {

}

@Composable
private fun SearchBody(
    query: String,
    recentSearches: List<RecentSearch>,
    searchResultState: SearchResultState,
    onUserClick: (UserItem) -> Unit,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onFollowToggle: (String) -> Unit,
    onPartyClick: (String) -> Unit,
) {
    LazyColumn {
        if (query.isEmpty()) {
            recentSearch(
                recentSearches = recentSearches,
                onDeleteRecentSearch = onDeleteRecentUser,
                onRecentSearchClick = onRecentSearchClick,
            )
        }

//        suggestions(
//            suggestions = uiState.suggestions,
//            onUserClicked = onUserClicked,
//            onFollowToggle = onFollowToggle,
//        )

        searchResult(
            query = query,
            searchResultState = searchResultState,
            onUserClick = onUserClick,
            onPartyClick = onPartyClick,
            onStoryClick = {},
        )
    }
}

private fun LazyListScope.suggestions(
    suggestions: List<UserSuggestion>,
    onUserClicked: (String) -> Unit,
    onFollowToggle: (String) -> Unit,
) {
    if (suggestions.isEmpty()) return

    item {
        Text(
            text = "Suggestions",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(16.dp),
        )
    }

    items(
        items = suggestions,
    ) { user ->
        UserSuggestionCard(
            modifier = Modifier.animateItem(),
            user = user,
            onUserClicked = onUserClicked,
            onFollowToggle = onFollowToggle,
        )
    }
}

private fun LazyListScope.recentSearch(
    recentSearches: List<RecentSearch>,
    onDeleteRecentSearch: (RecentSearch) -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
) {
    if (recentSearches.isEmpty()) return

    item {
        Text(
            text = "Recent",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(16.dp),
        )
    }

    items(
        items = recentSearches,
    ) { search ->
        RecentSearchComponent(
            recentSearch = search,
            onDeleteRecentUser = onDeleteRecentSearch,
            onClick = onRecentSearchClick,
        )
    }
}

private fun LazyListScope.searchResult(
    page: Int? = null,
    query: String,
    searchResultState: SearchResultState,
    onUserClick: (UserItem) -> Unit,
    onStoryClick: (String) -> Unit,
    onPartyClick: (String) -> Unit,
) {
    when (searchResultState) {
        SearchResultState.Loading -> {
            item {
                SearchLoadingScreen()
            }
        }

        is SearchResultState.SearchResults -> {
            val users = searchResultState.searchResult.users
            val parties = searchResultState.searchResult.parties

            if (users.isEmpty() && parties.isEmpty()) {
                item {
                    SearchEmptyScreen(
                        query = query,
                    )
                }
            }

            when (page) {
                null -> {
                    parties(
                        parties = parties,
                        onPartyClick = onPartyClick,
                    )
                    users(
                        users = users,
                        onStoryClick = onStoryClick,
                        onUserClick = onUserClick,
                    )
                }
                0 -> {
                    parties(
                        parties = parties,
                        onPartyClick = onPartyClick,
                    )
                }
                1 -> {
                    users(
                        users = users,
                        onStoryClick = onStoryClick,
                        onUserClick = onUserClick,
                    )
                }
            }
        }
    }
}

private fun LazyListScope.users(
    users: List<UserItem>,
    onUserClick: (UserItem) -> Unit,
    onStoryClick: (String) -> Unit,
) {
    items(
        items = users,
        key = { it.id },
    ) { user ->
        UserItem(
            userItem = user,
            modifier = Modifier.animateItem(),
            onClick = { onUserClick(user) },
            onStoryClick = onStoryClick,
        ) {
            FriendButton(
                isFriend = user.friend,
                onClick = {},
            )
        }
    }
}

private fun LazyListScope.parties(
    parties: List<Party>,
    onPartyClick: (String) -> Unit,
) {
    if (parties.isEmpty()) return

    items(
        count = parties.size,
        key = { parties[it].id },
    ) { i ->
        val party = parties[i]

        PartyItem(
            party = party,
            onClick = onPartyClick,
        )
    }
}

@Composable
fun UserSuggestionCard(
    modifier: Modifier = Modifier,
    user: UserSuggestion,
    onUserClicked: (String) -> Unit,
    onFollowToggle: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onUserClicked(user.username) }
            .padding(vertical = 6.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = user.picture)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        }).build()
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = Typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = Typography.bodyMedium
                )
            }
        }
        FriendButton(
            isFriend = user.followBack,
            onClick = { onFollowToggle(user.username) },
        )
    }
}

@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    user: User,
    onUserClicked: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onUserClicked(user.username) }
            .padding(vertical = 6.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = user.picture)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        }).build()
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = Typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = Typography.bodyMedium
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun SearchScreenPreview() {
    CheersPreview {
        SearchScreen(
            uiState = SearchUiState(),
            onSearchInputChanged = {},
            onRecentSearchClick = {},
            onBackPressed = {},
            onUserClick = {},
            onMapClick = {},
            onDeleteRecentUser = {},
            onFollowToggle = {},
            onSwipeRefresh = {},
            onSearch = {},
            onPartyClick = {},
        )
    }
}