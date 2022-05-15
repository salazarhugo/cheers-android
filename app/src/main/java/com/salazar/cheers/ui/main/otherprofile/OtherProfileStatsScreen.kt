package com.salazar.cheers.ui.main.otherprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.R
import com.salazar.cheers.components.Username
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.components.user.FollowButton
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun OtherProfileStatsScreen(
    uiState: OtherProfileStatsUiState,
    username: String,
    verified: Boolean,
    onSwipeRefresh: () -> Unit,
    onBackPressed: () -> Unit,
    onUserClicked: (username: String) -> Unit,
    onFollowToggle: (User) -> Unit,
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
            state = rememberSwipeToRefreshState(isRefreshing = false),
            onRefresh = onSwipeRefresh,
        ) {
            Column {
                Tabs(uiState, onUserClicked = onUserClicked, onFollowToggle)
            }
        }
    }
}

@Composable
fun Tabs(
    uiState: OtherProfileStatsUiState,
    onUserClicked: (username: String) -> Unit,
    onFollowToggle: (User) -> Unit,
) {
    val pages = if (uiState is OtherProfileStatsUiState.HasFollowers)
        listOf("${uiState.followers.size} followers", "${uiState.following.size} following")
    else
        listOf("Followers", "Following")

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
    if (uiState.isLoading)
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )
    SearchBar()
    HorizontalPager(
        count = pages.size,
        state = pagerState,
    ) { page ->
        Column(modifier = Modifier.fillMaxSize()) {
            when (page) {
                0 -> {
                    if (uiState is OtherProfileStatsUiState.HasFollowers)
                        Followers(followers = uiState.followers, onUserClicked)
                }
                1 -> {
                    if (uiState is OtherProfileStatsUiState.HasFollowers)
                        Following(following = uiState.following, onUserClicked, onFollowToggle)
                }
            }
        }
    }
}

@Composable
fun Followers(
    followers: List<User>,
    onUserClicked: (username: String) -> Unit,
) {
    LazyColumn {
        items(followers, key = { it.id }) { follower ->
            FollowerCard(follower, onUserClicked)
        }
    }
}

@Composable
fun Following(
    following: List<User>,
    onUserClicked: (username: String) -> Unit,
    onFollowToggle: (User) -> Unit,
) {
    LazyColumn {
        items(following, key = { it.id }) { following ->
            FollowingCard(following, onUserClicked, onFollowToggle = onFollowToggle)
        }
    }
}

@Composable
fun FollowerCard(
    user: User,
    onUserClicked: (username: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClicked(user.username) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = user.profilePictureUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        }).build()
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = Typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = Typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun FollowingCard(
    user: User,
    onUserClicked: (username: String) -> Unit,
    onFollowToggle: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClicked(user.username) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = user.profilePictureUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            transformations(CircleCropTransformation())
                            error(R.drawable.default_profile_picture)
                        }).build()
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = Typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = Typography.bodyMedium,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            var isFollowed by remember { mutableStateOf(user.isFollowed) }
            FollowButton(
                isFollowing = isFollowed,
                onClick = {
                    onFollowToggle(user)
                    isFollowed = !isFollowed
                }
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.MoreVert, null)
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
            shape = RoundedCornerShape(8.dp),
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
        SmallTopAppBar(
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
        )
    }
}