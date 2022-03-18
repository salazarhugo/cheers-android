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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.components.Username
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Typography

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearchInputChanged: (String) -> Unit,
    onUserClicked: (User) -> Unit,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onRecentUserClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = { SearchBar(uiState, onSearchInputChanged) },
    ) {
        SearchBody(
            uiState = uiState,
            onUserClicked = onUserClicked,
            onDeleteRecentUser = onDeleteRecentUser,
            onRecentUserClicked = onRecentUserClicked,
        )
    }
}

@Composable
private fun SearchBody(
    uiState: SearchUiState,
    onUserClicked: (User) -> Unit,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onRecentUserClicked: (String) -> Unit,
) {
    Column {
        val users = uiState.users
        val recommendation = uiState.userRecommendations
        if (users.isNullOrEmpty() && uiState.searchInput.isNotBlank()) {
            Text("No results")
            Spacer(Modifier.height(32.dp))
        } else if (users.isNotEmpty())
            UserList(users = users, onUserClicked = onUserClicked)
        RecentUserList(
            recent = uiState.recentUsers,
            recommendations = recommendation,
            onUserClicked = onUserClicked,
            onDeleteRecentUser = onDeleteRecentUser,
            onRecentUserClicked = onRecentUserClicked,
        )
    }
}

@Composable
fun UserList(
    users: List<User>,
    onUserClicked: (User) -> Unit
) {
    LazyColumn {
        item {
            Text(
                text = "Result",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
            )
//            Spacer(Modifier.height(8.dp))
        }
        items(users) { user ->
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
    recommendations: List<User>,
    onUserClicked: (User) -> Unit,
    onDeleteRecentUser: (RecentUser) -> Unit,
    onRecentUserClicked: (String) -> Unit,
) {
    LazyColumn {
        if (recent.isNotEmpty())
            item {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                )
            }
        items(recent) { user ->
            RecentUserCard(user, onDeleteRecentUser = onDeleteRecentUser, onRecentUserClicked)
        }
        if (recommendations.isNotEmpty())
            item {
                Text(
                    text = "Suggestions",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                )
            }
        items(recommendations) { user ->
            UserCard(
                modifier = Modifier.animateItemPlacement(),
                user = user,
                onUserClicked = onUserClicked,
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
                painter = rememberImagePainter(
                    data = user.profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
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
fun UserCard(
    modifier: Modifier = Modifier,
    user: User,
    onUserClicked: (User) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onUserClicked(user) }
            .padding(vertical = 6.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(
                    data = user.profilePictureUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    },
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
//                    Text(text = user.username, style = Typography.bodyMedium)
            }
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Close, null)
        }
    }
}

@Composable
fun SearchBar(
    uiState: SearchUiState,
    onSearchInputChanged: (String) -> Unit
) {
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

        val searchInput = uiState.searchInput
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

