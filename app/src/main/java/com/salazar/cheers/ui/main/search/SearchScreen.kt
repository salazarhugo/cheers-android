package com.salazar.cheers.ui.main.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.salazar.cheers.R
import com.salazar.cheers.components.CircularProgressIndicatorM3
import com.salazar.cheers.components.Username
import com.salazar.cheers.components.share.SwipeToRefresh
import com.salazar.cheers.components.share.rememberSwipeToRefreshState
import com.salazar.cheers.components.user.FollowButton
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.data.entities.UserSuggestion
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.home.likes.UserList
import com.salazar.cheers.ui.theme.Typography

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearchInputChanged: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onSwipeRefresh: () -> Unit,
    onRecentUserClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier.padding(16.dp),
                searchInput = uiState.searchInput,
                onSearchInputChanged = onSearchInputChanged,
            )
        },
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(isRefreshing = false),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(it),
        ) {
            SearchBody(
                uiState = uiState,
                onUserClicked = onUserClicked,
                onDeleteRecentUser = onDeleteRecentUser,
                onRecentUserClicked = onRecentUserClicked,
            )
        }
    }
}

@Composable
private fun SearchBody(
    uiState: SearchUiState,
    onUserClicked: (String) -> Unit,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onRecentUserClicked: (String) -> Unit,
) {
    Column {
        val users = uiState.users
        val recommendation = uiState.userRecommendations
//        if (users.isNullOrEmpty() && uiState.searchInput.isNotBlank()) {
//            Text("No results")
//            Spacer(Modifier.height(32.dp))
        if (uiState.searchInput.isBlank())
            RecentUserList(
                recent = uiState.recentUsers,
                suggestions = uiState.userRecommendations,
                onUserClicked = onUserClicked,
                onDeleteRecentUser = onDeleteRecentUser,
                onRecentUserClicked = onRecentUserClicked,
            )
        else if (users.isNotEmpty())
            UserList(
                users = users,
                onUserClicked = onUserClicked,
                isLoading = uiState.isLoading,
            )
    }
}

@Composable
fun UserList(
    users: List<User>,
    isLoading: Boolean,
    onUserClicked: (String) -> Unit,
) {
    LazyColumn {
        item {
            Text(
                text = "Result",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(16.dp),
            )
        }
        if (isLoading)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicatorM3()
                }
            }
        items(users, key = { it.id }) { user ->
            UserCard(
                modifier = Modifier.animateItemPlacement(),
                user = user,
                onUserClicked = onUserClicked,
            )
        }
    }
}

@Composable
fun RecentUserList(
    recent: List<RecentUser>,
    suggestions: List<UserSuggestion>,
    onUserClicked: (String) -> Unit,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onRecentUserClicked: (String) -> Unit,
) {
    LazyColumn {
        if (recent.isNotEmpty())
            item {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.padding(16.dp),
                )
            }
        items(recent, key = { it.id }) { user ->
            RecentUserCard(user, onDeleteRecentUser = onDeleteRecentUser, onRecentUserClicked)
        }
        if (suggestions.isNotEmpty())
            item {
                Text(
                    text = "Suggestions",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                )
            }
        items(suggestions, key = { it.id }) { user ->
            UserSuggestionCard(
                modifier = Modifier.animateItemPlacement(),
                user = user,
                onUserClicked = onUserClicked,
                onFollowToggle = {},
            )
        }
    }
}

@Composable
fun RecentUserCard(
    user: RecentUser,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onRecentUserClicked: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecentUserClicked(user.username) }
            .padding(vertical = 6.dp, horizontal = 16.dp),
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
                    .size(54.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.fullName.isNotBlank())
                    Text(text = user.fullName, style = Typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = Typography.bodyMedium
                )
            }
        }
        IconButton(onClick = { onDeleteRecentUser(user) }) {
            Icon(Icons.Default.Close, null)
        }
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
                    ImageRequest.Builder(LocalContext.current).data(data = user.avatar)
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
        FollowButton(isFollowing = user.followBack, onClick = { onFollowToggle(user.username) })
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
                    ImageRequest.Builder(LocalContext.current).data(data = user.profilePictureUrl)
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

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchInput: String,
    onSearchInputChanged: (String) -> Unit
) {
    Box(
        modifier = modifier,
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

        val focusManager = LocalFocusManager.current

        TextField(
            value = searchInput,
            leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { onSearchInputChanged(it) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    fontSize = 13.sp
            ),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
            }),
            placeholder = { Text("Search") },
            trailingIcon = {
                if (searchInput.isNotBlank())
                    Icon(Icons.Filled.Close, null,
                        Modifier.clickable { onSearchInputChanged("") })
            }
        )
    }
}

