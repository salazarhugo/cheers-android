package com.salazar.cheers.core.ui.components.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.searchbar.SearchBarComponent

@Composable
fun ShareBottomSheet(
    link: String,
    viewModel: ShareViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val users = uiState.users
    val selectedUsers = uiState.selectedUsers

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
    ) {
        Scaffold(
            bottomBar = {
                ShareBottomBar(
                    modifier = Modifier.fillMaxWidth(),
                    onSend = {
                        viewModel.onSend(link)
                    },
                )
            }
        ) {
            Column(
                modifier = Modifier.padding(it),
            ) {
                ShareScreen(
                    users = users,
                    selectedUsers = selectedUsers,
                    onSelectUser = {
                        viewModel.onSelectUser(it)
                    }
                )
            }
        }
    }
}

@Composable
fun ShareScreen(
    users: List<UserItem>,
    selectedUsers: List<UserItem>,
    onSelectUser: (UserItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBarComponent(
        modifier = modifier.fillMaxWidth(),
        searchInput = "",
        onSearchInputChanged = {},
    )
    LazyColumn {
        items(
            items = users,
            key = { it.id },
        ) { user ->
            UserItem(
                userItem = user,
                onClick = {
                    onSelectUser(user)
                },
                onStoryClick = {},
                content = {
                    val selected = selectedUsers.contains(user)
                    Checkbox(
                        checked = selected,
                        onCheckedChange = {
                            onSelectUser(user)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                    )
                }
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun ShareBottomSheetPreview() {
    CheersPreview {
        ShareBottomSheet(
            link = "",
            modifier = Modifier,
        )
    }
}
