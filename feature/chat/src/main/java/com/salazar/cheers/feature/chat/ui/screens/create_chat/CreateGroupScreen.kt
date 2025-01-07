package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.CheersSearchBar
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.core.ui.ui.Username

@Composable
fun CreateGroupScreen(
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
                title = "New Group",
                onBackPressed = onBackPressed,
            )
        },
        bottomBar = {
            val text = if (uiState.selectedUsers.size > 1) "Group with Group" else "Group"
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
            CheersSearchBar(
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
fun UserCard(
    user: UserItem,
    modifier: Modifier = Modifier,
    selected: Boolean,
    onUserCheckedChange: (UserItem) -> Unit,
) {
    val color = when (selected) {
        true -> BlueCheers
        false -> MaterialTheme.colorScheme.onBackground
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onUserCheckedChange(user) }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarComponent(
                name = user.name,
                username = user.username,
                avatar = user.picture,
                size = 50.dp,
            )
            Spacer(
                modifier = Modifier.width(12.dp),
            )
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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
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

