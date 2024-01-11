package com.salazar.cheers.feature.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.theme.Typography
import com.salazar.cheers.core.ui.ui.SwipeToRefresh
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.ui.ui.rememberSwipeToRefreshState
import com.salazar.cheers.data.user.RecentUser
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserSuggestion

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
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(16.dp),
                query = uiState.searchInput,
                onQueryChange = onSearchInputChanged,
                onSearch = {},
                active = false,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                placeholder = {
                    Text(text = "Search people")
                },
                onActiveChange = {},
            ) {
            }
        },
    ) {
        SwipeToRefresh(
            state = rememberSwipeToRefreshState(uiState.isLoading),
            onRefresh = onSwipeRefresh,
            modifier = Modifier.padding(top = it.calculateTopPadding()),
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

            items(uiState.recentUsers) { user ->
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
