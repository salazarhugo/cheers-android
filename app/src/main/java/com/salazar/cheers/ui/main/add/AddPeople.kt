package com.salazar.cheers.ui.main.add

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.compose.ChipGroup
import com.salazar.cheers.compose.LoadingScreen
import com.salazar.cheers.compose.Username
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.main.taguser.AddPeopleViewModel
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography

@Composable
fun AddPeopleScreen(
    onSelectUser: (User) -> Unit,
    selectedUsers: List<User>,
    onBackPressed: () -> Unit,
    onDone: () -> Unit,
) {
    val viewModel = hiltViewModel<AddPeopleViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading)
        LoadingScreen()

    Scaffold(
        topBar = {
            AddPeopleTopBar(
                onDismiss = onBackPressed,
                onDone = onDone
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .animateContentSize()
        ) {
//            if(uiState.selectedUsers.isNotEmpty())
            ChipInput(
                selectedUsers = selectedUsers,
            )
            SearchTextInput(
                searchInput = uiState.searchInput,
                selectedUsers = uiState.selectedUsers,
                onSearchInputChanged = viewModel::onSearchInputChanged,
            )
            if (uiState.users != null)
                Users(
                    users = uiState.users!!,
                    selectedUsers = selectedUsers,
                    onSelectUser = onSelectUser,
                )
        }
    }
}

@Composable
fun AddPeopleTopBar(
    onDismiss: () -> Unit,
    onDone: () -> Unit
) {
    SmallTopAppBar(
        title = { Text("Add people", fontWeight = FontWeight.Bold, fontFamily = Roboto) },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        },
        actions = {
            TextButton(onClick = onDone) {
                Text("DONE")
            }
        }
    )
}

@Composable
fun Users(
    users: List<User>,
    selectedUsers: List<User>,
    onSelectUser: (User) -> Unit,
) {
    LazyColumn {
        items(users, key = { it.id }) { user ->
            UserCard(user, selectedUsers.contains(user), onSelectUser = onSelectUser)
        }
    }
}

@Composable
fun UserCard(
    user: User,
    selected: Boolean,
    onSelectUser: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectUser(user) }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserProfilePicture(avatar = user.picture)
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
        Checkbox(
            checked = selected,
            onCheckedChange = { onSelectUser(user) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        )
    }
}

@Composable
fun SearchTextInput(
    searchInput: String,
    selectedUsers: List<User>,
    onSearchInputChanged: (String) -> Unit,
) {
    TextField(
        value = searchInput,
        leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onSearchInputChanged,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colorScheme.background,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onSearch = {
        }),
        placeholder = { Text("Search") }
    )
}

@Composable
fun ChipInput(
    selectedUsers: List<User>,
) {
    ChipGroup(
        users = selectedUsers.map { it.username },
        onSelectedChanged = { }
    )
}