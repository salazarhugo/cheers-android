package com.salazar.cheers.ui.main.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.compose.LoadingScreen
import com.salazar.cheers.ui.compose.Username
import com.salazar.cheers.ui.compose.items.UserItem
import com.salazar.cheers.ui.compose.share.SwipeToRefresh
import com.salazar.cheers.ui.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.ui.compose.user.FollowButton
import com.salazar.cheers.ui.theme.Roboto
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
            Toolbar(
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
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        // Add tabs for all of our pages
        pages.forEachIndexed { index, title ->
            Tab(
                text = { Text(title, style = MaterialTheme.typography.bodyMedium) },
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
    SearchBar()
    HorizontalPager(
        count = pages.size,
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
    followers: List<UserItem>?,
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
                        Text("Remove")
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
            items(following, key = { it.id }) { user ->
                UserItem(
                    userItem = user,
                    onClick = onUserClicked,
                    onStoryClick = onStoryClick,
                ) {
//                    FollowButton(isFollowing = user.has_followed, onClick = { onFollowToggle(user.id)})
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
            elevation = 0.dp,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        ) {}
        val query = remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        TextField(
            value = query.value,
            leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
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
fun Toolbar(
    username: String,
    verified: Boolean,
    onBackPressed: () -> Unit,
) {
    Column {
        TopAppBar(title = {
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
            })
    }
}

