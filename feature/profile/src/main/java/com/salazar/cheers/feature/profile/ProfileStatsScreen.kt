package com.salazar.cheers.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import kotlinx.coroutines.launch

@Composable
fun ProfileStatsScreen(
    uiState: ProfileStatsUiState,
    username: String,
    verified: Boolean,
    onBackPressed: () -> Unit,
    onUserClicked: (username: String) -> Unit,
    onStoryClick: (username: String) -> Unit,
    onFollowToggle: (String) -> Unit,
    onSwipeRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            ProfileTopBar(
                username = username,
                verified = verified,
                onBackPressed = onBackPressed
            )
        }
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoadingFollowers || uiState.isLoadingFollowing),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(it),
        ) {
            Column {
                Tabs(
                    uiState = uiState,
                    onUserClicked = onUserClicked,
                    onFollowToggle = onFollowToggle,
                    onStoryClick = onStoryClick,
                )
            }
        }
    }
}

@Composable
fun Tabs(
    uiState: ProfileStatsUiState,
    onUserClicked: (username: String) -> Unit,
    onStoryClick: (username: String) -> Unit,
    onFollowToggle: (String) -> Unit,
) {
    val followersTitle =
        if (uiState.followers == null) "Followers" else "${uiState.followers.size} followers"
    val followingTitle =
        if (uiState.following == null) "Following" else "${uiState.following.size} following"

    val pages = listOf(followersTitle, followingTitle)
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
    )
    val scope = rememberCoroutineScope()

    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
            )
        },
//        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        // Add tabs for all of our pages
        pages.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
//                    viewModel.toggle()
                },
            )
        }
    }
//    SearchBar()
    HorizontalPager(
        state = pagerState,
    ) { page ->
        Column(modifier = Modifier.fillMaxSize()) {
            when (page) {
                0 -> Followers(
                    followers = uiState.followers,
                    onUserClicked = onUserClicked,
                    onStoryClick = onStoryClick,
                )
                1 -> Following(
                    following = uiState.following,
                    onUserClicked = onUserClicked,
                    onStoryClick = onStoryClick,
                    onFollowToggle = onFollowToggle,
                )
            }
        }
    }
}

@Composable
fun Followers(
    followers: List<com.salazar.cheers.core.model.UserItem>?,
    onUserClicked: (username: String) -> Unit,
    onStoryClick: (username: String) -> Unit,
) {
    if (followers == null) {
        LoadingScreen()
    } else
        LazyColumn {
            items(followers, key = { it.id }) { follower ->
                UserItem(
                    userItem = follower,
                    onClick = onUserClicked,
                    onStoryClick = onStoryClick,
                ) {
                    OutlinedButton(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.height(34.dp),
                        onClick = { /* TODO */ }
                    ) {
                        Text(
                            text = "Remove",
                        )
                    }
                }
            }
        }
}

@Composable
fun Following(
    following: List<UserItem>?,
    onUserClicked: (username: String) -> Unit,
    onStoryClick: (username: String) -> Unit,
    onFollowToggle: (String) -> Unit,
) {
    if (following == null) {
        LoadingScreen()
    } else
        LazyColumn {
            items(
                items = following,
                key = { it.id },
            ) { user ->
                UserItem(
                    userItem = user,
                    onClick = onUserClicked,
                    onStoryClick = onStoryClick,
                ) {
                    FriendButton(
                        isFriend = user.friend,
                        onClick = { onFollowToggle(user.id) },
                    )
                }
            }
        }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier.padding(15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
//            elevation = 0.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
//            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        ) {}
        val query = remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        TextField(
            value = query.value,
            leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            onValueChange = {
                query.value = it
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
            }),
            placeholder = { Text("Search") },
            trailingIcon = {
                if (query.value.isNotBlank())
                    Icon(Icons.Filled.Close, null,
                        Modifier.clickable { query.value = "" })
            }
        )
    }
}

@Composable
fun ProfileTopBar(
    username: String,
    verified: Boolean,
    onBackPressed: () -> Unit,
    onMenuClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Username(
                username = username,
                verified = verified,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto
                ),
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Outlined.ArrowBack, "")
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Outlined.Menu, null)
            }
        }
    )
}

