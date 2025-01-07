package com.salazar.cheers.feature.search

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
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
import com.salazar.cheers.core.ui.components.UserItemListLoading
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent
import com.salazar.cheers.core.ui.extensions.noRippleClickable
import com.salazar.cheers.core.ui.item.party.PartyItem
import com.salazar.cheers.core.ui.item.party.PartyItemListLoading
import com.salazar.cheers.core.ui.theme.Typography
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.feature.search.components.RecentSearchComponent
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearchInputChanged: (String) -> Unit,
    onUserClick: (UserItem) -> Unit,
    onPartyClick: (Party) -> Unit,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onSwipeRefresh: () -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onFollowToggle: (String) -> Unit,
    onMapClick: () -> Unit,
    onSearch: (String) -> Unit,
    onBackPressed: () -> Unit,
    onClearRecentClick: () -> Unit,
) {
    val onActiveChange = { a: Boolean -> Unit }
    val colors1 = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        dividerColor = Color.Transparent,
    )
    val query = uiState.searchInput
    val searchResultState = uiState.searchResultState
    val tabs = listOf(
        "Parties",
        "Users",
        "Chats",
        "Music",
        "Links",
    )
    val pagerState = rememberPagerState { tabs.size }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    PredictiveBackHandler(true) { progress ->
        // code for gesture back started
        try {
            progress.collect { backEvent ->
            }
            onBackPressed()
        } catch (e: CancellationException) {
            e.printStackTrace()
        }
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
                    query = query,
                    modifier = Modifier.focusRequester(focusRequester),
                    onQueryChange = onSearchInputChanged,
                    onSearch = {
                        focusManager.clearFocus()
                        onSearch(it)
                    },
                    expanded = true,
                    onExpandedChange = onActiveChange,
                    enabled = true,
                    placeholder = {
                        Text(text = stringResource(com.salazar.cheers.core.ui.R.string.search))
                    },
                    leadingIcon = { SearchLeadingIcon() },
                    trailingIcon = {
                        SearchTrailingIcon(
                            isQueryEmpty = query.isEmpty(),
                            onClearClick = { onSearchInputChanged("") },
                        )
                    },
                )
            },
            expanded = true,
            onExpandedChange = onActiveChange,
            shape = SearchBarDefaults.inputFieldShape,
            colors = colors1,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
            content = {
                SearchBody(
                    query = query,
                    tabs = tabs,
                    pagerState = pagerState,
                    recentSearches = uiState.recentSearch,
                    searchResultState = searchResultState,
                    onUserClick = onUserClick,
                    onDeleteRecentUser = onDeleteRecentUser,
                    onRecentSearchClick = onRecentSearchClick,
                    onFollowToggle = onFollowToggle,
                    onPartyClick = onPartyClick,
                    onClearRecentClick = onClearRecentClick,
                )
            },
        )
    }
}

@Composable
private fun SearchLeadingIcon() {
    Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search icon",
    )
}

@Composable
private fun SearchTrailingIcon(
    isQueryEmpty: Boolean,
    onClearClick: () -> Unit,
) {
    if (isQueryEmpty) return

    IconButton(onClick = onClearClick) {
        Icon(
            imageVector = Icons.Outlined.Close,
            contentDescription = "Clear icon",
        )
    }
}

@Composable
private fun SearchBody(
    query: String,
    tabs: List<String>,
    pagerState: PagerState,
    recentSearches: List<RecentSearch>,
    searchResultState: SearchResultState,
    onUserClick: (UserItem) -> Unit,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onFollowToggle: (String) -> Unit,
    onPartyClick: (Party) -> Unit,
    onClearRecentClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    PrimaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        contentColor = MaterialTheme.colorScheme.onBackground,
        containerColor = Color.Transparent,
        edgePadding = 0.dp,
        divider = {},
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = pagerState.currentPage == index
            val color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Tab(
                selected = selected,
                text = {
                    Text(
                        text = tab,
                        color = color,
                    )
                },
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
                recentSearches = recentSearches,
                page = page,
                query = query,
                searchResultState = searchResultState,
                onUserClick = onUserClick,
                onPartyClick = onPartyClick,
                onStoryClick = {},
                onRecentSearchClick = onRecentSearchClick,
                onDeleteRecentUser = onDeleteRecentUser,
                onClearRecentClick = onClearRecentClick,
            )
        }
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
    onClearClick: () -> Unit,
) {
    if (recentSearches.isEmpty()) return

    item {
        Row(
            modifier = Modifier
                .animateItem()
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceDim)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Recent",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                modifier = Modifier.noRippleClickable { onClearClick() },
                text = "Clear",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    items(
        items = recentSearches,
    ) { search ->
        RecentSearchComponent(
            modifier = Modifier.animateItem(),
            recentSearch = search,
            onDeleteRecentUser = onDeleteRecentSearch,
            onClick = onRecentSearchClick,
        )
    }
}

private fun LazyListScope.searchResult(
    page: Int? = null,
    query: String,
    recentSearches: List<RecentSearch>,
    searchResultState: SearchResultState,
    onUserClick: (UserItem) -> Unit,
    onStoryClick: (String) -> Unit,
    onPartyClick: (Party) -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onClearRecentClick: () -> Unit,
) {
    val isLoading = searchResultState is SearchResultState.Loading
    val searchResult = (searchResultState as? SearchResultState.SearchResults)?.searchResult
    val users = searchResult?.users
    val parties = searchResult?.parties
    val isQueryEmpty = query.count() <= 2

    when (page) {
        0 -> {
            parties(
                isQueryEmpty = isQueryEmpty,
                recentParties = recentSearches.filterIsInstance<RecentSearch.Party>(),
                isLoading = isLoading,
                parties = parties,
                onPartyClick = onPartyClick,
                onRecentSearchClick = onRecentSearchClick,
                onDeleteRecentUser = onDeleteRecentUser,
                onClearRecentClick = onClearRecentClick,
            )
        }

        1 -> {
            users(
                isQueryEmpty = isQueryEmpty,
                recentUsers = recentSearches.filterIsInstance<RecentSearch.User>(),
                isLoading = isLoading,
                users = users,
                onStoryClick = onStoryClick,
                onUserClick = onUserClick,
                onRecentSearchClick = onRecentSearchClick,
                onDeleteRecentUser = onDeleteRecentUser,
                onClearRecentClick = onClearRecentClick,
            )
        }
    }
}

private fun LazyListScope.users(
    isQueryEmpty: Boolean,
    isLoading: Boolean,
    recentUsers: List<RecentSearch.User>,
    users: List<UserItem>?,
    onUserClick: (UserItem) -> Unit,
    onStoryClick: (String) -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onClearRecentClick: () -> Unit,
) {
    if (isLoading) {
        item {
            UserItemListLoading()
        }
    } else if (isQueryEmpty) {
        recentSearch(
            recentSearches = recentUsers,
            onRecentSearchClick = onRecentSearchClick,
            onDeleteRecentSearch = onDeleteRecentUser,
            onClearClick = onClearRecentClick,
        )
    } else if (users?.isEmpty() == true) {
        emptyResults()
    }

    if (users.isNullOrEmpty()) {
        return
    }

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

private fun LazyListScope.emptyResults() {
    item {
        EmptyResults(
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun LazyListScope.parties(
    isQueryEmpty: Boolean,
    isLoading: Boolean,
    recentParties: List<RecentSearch.Party>,
    parties: List<Party>?,
    onPartyClick: (Party) -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onDeleteRecentUser: (RecentSearch) -> Unit,
    onClearRecentClick: () -> Unit,
) {
    if (isLoading) {
        item {
            PartyItemListLoading()
        }
    } else if (isQueryEmpty) {
        recentSearch(
            recentSearches = recentParties,
            onRecentSearchClick = onRecentSearchClick,
            onDeleteRecentSearch = onDeleteRecentUser,
            onClearClick = onClearRecentClick,
        )
    } else if (parties?.isEmpty() == true) {
        emptyResults()
    }

    if (parties.isNullOrEmpty()) {
        return
    }

    item {
        Row(
            modifier = Modifier
                .animateItem()
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceDim)
        ) {
            Text(
                text = "Results",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }

    items(
        count = parties.size,
        key = { parties[it].id },
    ) { i ->
        val party = parties[i]

        PartyItem(
            party = party,
            onClick = {
                onPartyClick(party)
            },
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
            onClearRecentClick = {},
        )
    }
}

@Composable
fun EmptyResults(
    modifier: Modifier = Modifier,
) {
    MessageScreenComponent(
        modifier = modifier.padding(16.dp),
        title = "No Results",
        subtitle = "Try a new search",
    )
}
