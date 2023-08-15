package com.salazar.cheers.feature.create_post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.ChipGroup
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.theme.Typography
import com.salazar.cheers.core.ui.ui.UserProfilePicture

@Composable
fun AddPeopleScreen(
    selectedUsers: List<UserItem>,
    onSelectUser: (UserItem) -> Unit,
    onBackPressed: () -> Unit,
    onDone: () -> Unit,
) {
    val viewModel = hiltViewModel<AddPeopleViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading)
        com.salazar.cheers.core.share.ui.LoadingScreen()

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
    TopAppBar(title = { Text("Add people", fontWeight = FontWeight.Bold, fontFamily = Roboto) },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        },
        actions = {
            TextButton(onClick = onDone) {
                Text("DONE")
            }
        })
}

@Composable
fun Users(
    users: List<UserItem>,
    selectedUsers: List<UserItem>,
    onSelectUser: (UserItem) -> Unit,
) {
    LazyColumn {
        items(users, key = { it.id }) { user ->
            UserCard(user, selectedUsers.contains(user), onSelectUser = onSelectUser)
        }
    }
}

@Composable
fun UserCard(
    user: com.salazar.cheers.core.model.UserItem,
    selected: Boolean,
    onSelectUser: (com.salazar.cheers.core.model.UserItem) -> Unit,
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
            UserProfilePicture(picture = user.picture)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = Typography.bodyMedium)
                com.salazar.cheers.core.share.ui.Username(
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
    selectedUsers: List<com.salazar.cheers.core.model.UserItem>,
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
        colors = TextFieldDefaults.colors(
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
    selectedUsers: List<UserItem>,
) {
    ChipGroup(
        users = selectedUsers.map { it.username },
        onSelectedChanged = { }
    )
}