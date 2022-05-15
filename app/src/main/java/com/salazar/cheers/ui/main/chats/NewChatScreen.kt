package com.salazar.cheers.ui.main.chats

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
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
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.BlueCheers
import com.salazar.cheers.ui.theme.Typography

@Composable
fun NewChatScreen(
    uiState: NewChatUiState,
    onNewGroupClick: () -> Unit,
    onFabClick: () -> Unit,
    onUserCheckedChange: (User) -> Unit,
    onQueryChange: (String) -> Unit,
    onGroupNameChange: (String) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onFabClick,
            ) {
                val text = if (uiState.selectedUsers.size > 1) "Chat with Group" else "Chat"
                Text(text = text)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            SearchBar(searchInput = uiState.query, onSearchInputChanged = onQueryChange)
            ChipGroup(
                users = uiState.selectedUsers.map { it.name.ifBlank { it.username } },
                onSelectedChanged = { name -> },
                unselectedColor = MaterialTheme.colorScheme.outline,
            )
            if (uiState.selectedUsers.size > 1 || uiState.isGroup)
                NewGroupNameInput(
                    groupName = uiState.groupName,
                    onGroupNameChange = onGroupNameChange,
                )
            else
                NewGroupButton(onNewGroupClick = onNewGroupClick)
            LazyColumn {
                stickyHeader {
                    val text = if (uiState.query.isBlank()) "Recents" else "Friends"
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(16.dp),
                    )
                }
                item {
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tonalElevation = 8.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column {
                            if (uiState.query.isBlank())
                                uiState.recentUsers.forEach { user ->
                                    UserCard(
                                        user = user,
                                        selected = uiState.selectedUsers.contains(user),
                                        onUserCheckedChange = onUserCheckedChange,
                                    )
                                }
                            else
                                uiState.users.forEach { user ->
                                    UserCard(
                                        user = user,
                                        selected = uiState.selectedUsers.contains(user),
                                        onUserCheckedChange = onUserCheckedChange,
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewGroupButton(
    onNewGroupClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.padding(16.dp),
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNewGroupClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.GroupAdd, contentDescription = null)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "New Group",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "Chat with up to 100 friends",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun UserCard(
    user: User,
    selected: Boolean,
    onUserCheckedChange: (User) -> Unit,
) {
    val color = if (selected) BlueCheers else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserCheckedChange(user) }
            .padding(horizontal = 16.dp, vertical = 6.dp),
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
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = Typography.bodyMedium.copy(color = color))
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = Typography.bodyMedium
                )
            }
        }
        Checkbox(
            checked = selected,
            onCheckedChange = { onUserCheckedChange(user) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        )
    }
}

@Composable
fun NewGroupNameInput(
    groupName: String,
    onGroupNameChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(15.dp),
        contentAlignment = Alignment.Center,
    ) {
        val focusManager = LocalFocusManager.current

        TextField(
            value = groupName,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent
            ),
            isError = groupName.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { onGroupNameChange(it) },
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
            placeholder = {
                Text(
                    text = "New Group Name"
                )
            },
        )
    }
}

@Composable
fun SearchBar(
    searchInput: String,
    onSearchInputChanged: (String) -> Unit
) {
    Box(
        modifier = Modifier.padding(15.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material.Card(
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
            leadingIcon = { Text("To:") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
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
            trailingIcon = {
                if (searchInput.isNotBlank())
                    Icon(Icons.Filled.Close, null,
                        Modifier.clickable { onSearchInputChanged("") })
            }
        )
    }
}

