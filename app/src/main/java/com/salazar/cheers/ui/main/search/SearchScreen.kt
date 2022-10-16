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
import com.salazar.cheers.compose.CircularProgressIndicatorM3
import com.salazar.cheers.compose.Username
import com.salazar.cheers.compose.items.UserItem
import com.salazar.cheers.compose.share.SwipeToRefresh
import com.salazar.cheers.compose.share.rememberSwipeToRefreshState
import com.salazar.cheers.compose.user.FollowButton
import com.salazar.cheers.data.db.entities.RecentUser
import com.salazar.cheers.data.db.entities.UserSuggestion
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Typography

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearchInputChanged: (String) -> Unit,
    onUserClicked: (String) -> Unit,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onSwipeRefresh: () -> Unit,
    onRecentUserClicked: (String) -> Unit,
    onFollowToggle: (String) -> Unit,
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
                onFollowToggle = onFollowToggle,
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
    onFollowToggle: (String) -> Unit,
) {
    LazyColumn {
        if (uiState.searchInput.isBlank()) {
            if (uiState.recentUsers.isNotEmpty())
                item {
                    Text(
                        text = "Recent",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(16.dp),
                    )
                }

            items(uiState.recentUsers, key = { it.username }) { user ->
                RecentUserCard(user, onDeleteRecentUser = onDeleteRecentUser, onRecentUserClicked)
            }

            if (uiState.suggestions.isNotEmpty())
                item {
                    Text(
                        text = "Suggestions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(16.dp),
                    )
                }

            items(uiState.suggestions) { user ->
                UserSuggestionCard(
                    modifier = Modifier.animateItemPlacement(),
                    user = user,
                    onUserClicked = onUserClicked,
                    onFollowToggle = onFollowToggle,
                )
            }

        }

        item {
            Text(
                text = "Result",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(16.dp),
            )
        }

        if (uiState.isLoading)
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

        if (uiState.users != null) {
            if (uiState.users.isEmpty() && !uiState.isLoading)
                item() {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "No results for this query"
                    )
                }
            else
                items(uiState.users, key = { it.id }) { user ->
                    UserItem(
                        modifier = Modifier.animateItemPlacement(),
                        userItem = user,
                        onClick = onUserClicked,
                    )
                }
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

