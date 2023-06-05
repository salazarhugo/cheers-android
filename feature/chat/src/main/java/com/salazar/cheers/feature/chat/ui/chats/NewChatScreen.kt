package com.salazar.cheers.feature.chat.ui.chats

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.share.ui.Username
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.feature.chat.R

@Composable
fun NewChatScreen(
    uiState: NewChatUiState,
    onNewGroupClick: () -> Unit,
    onFabClick: () -> Unit,
    onUserCheckedChange: (UserItem) -> Unit,
    onQueryChange: (String) -> Unit,
    onGroupNameChange: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "Create Chat",
                onBackPressed = onBackPressed,
            )
        },
        bottomBar = {
            val text = if (uiState.selectedUsers.size > 1) "Chat with Group" else "Chat"
            ButtonWithLoading(
                text = text,
                isLoading = uiState.isLoading,
                onClick = onFabClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(12.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = uiState.selectedUsers.isNotEmpty(),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .animateContentSize(),
        ) {
            SearchBar(
                searchInput = uiState.query,
                modifier = Modifier.padding(16.dp),
                onSearchInputChanged = onQueryChange,
                leadingIcon = { Text("To:") },
            )
//            ChipGroup(
//                users = uiState.selectedUsers.map { it.name.ifBlank { it.username } },
//                onSelectedChanged = { name -> },
//            )
            if (uiState.selectedUsers.size > 1 || uiState.isGroup)
                NewGroupNameInput(
                    groupName = uiState.groupName,
                    onGroupNameChange = onGroupNameChange,
                )
            else
                NewGroupButton(onNewGroupClick = onNewGroupClick)
            LazyColumn {
                item {
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tonalElevation = 8.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column {
                            uiState.users?.forEach { user ->
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
        shape = MaterialTheme.shapes.medium
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
    user: UserItem,
    selected: Boolean,
    onUserCheckedChange: (UserItem) -> Unit,
) {
    val color =
        if (selected) com.salazar.cheers.core.share.ui.BlueCheers else MaterialTheme.colorScheme.onBackground

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
                    ImageRequest.Builder(LocalContext.current).data(data = user.picture)
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
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyMedium.copy(color = color),
                    )
                Username(
                    username = user.username,
                    verified = user.verified,
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
    modifier: Modifier = Modifier,
    onSearchInputChanged: (String) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    autoFocus: Boolean = false,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
        ) {}

        val focusManager = LocalFocusManager.current

        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            if (autoFocus)
                focusRequester.requestFocus()
        }

        TextField(
            value = searchInput,
            leadingIcon = leadingIcon,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
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
            placeholder = placeholder,
            trailingIcon = {
                if (searchInput.isNotBlank())
                    Icon(Icons.Filled.Close, null,
                        Modifier.clickable { onSearchInputChanged("") })
            },
        )
    }
}

