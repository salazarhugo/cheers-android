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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.ChipGroup
import com.salazar.cheers.core.ui.components.searchbar.SearchBarComponent
import com.salazar.cheers.core.ui.theme.Typography
import com.salazar.cheers.core.ui.ui.LoadingScreen
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.UserProfilePicture
import com.salazar.cheers.core.ui.ui.Username

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
            SearchBarComponent(
                modifier = Modifier.fillMaxWidth(),
                searchInput = uiState.searchInput,
                onSearchInputChanged = viewModel::onSearchInputChanged,
            )
            if (uiState.users != null) {
                Users(
                    users = uiState.users!!,
                    selectedUsers = selectedUsers,
                    onSelectUser = onSelectUser,
                )
            }
        }
    }
}

@Composable
fun AddPeopleTopBar(
    onDismiss: () -> Unit,
    onDone: () -> Unit
) {
    Toolbar(
        title = "Add people",
        onBackPressed = onDismiss,
        actions = {
            TextButton(onClick = onDone) {
                Text("Done")
            }
        }
    )
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
    user: UserItem,
    selected: Boolean,
    onSelectUser: (UserItem) -> Unit,
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
                if (user.name.isNotBlank()) {
                    Text(
                        text = user.name,
                        style = Typography.bodyMedium,
                    )
                }
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
fun ChipInput(
    selectedUsers: List<UserItem>,
) {
    ChipGroup(
        users = selectedUsers.map { it.username },
        onSelectedChanged = { }
    )
}