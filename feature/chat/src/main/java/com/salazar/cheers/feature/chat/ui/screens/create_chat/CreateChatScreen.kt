package com.salazar.cheers.feature.chat.ui.screens.create_chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.CheersSearchBar
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun CreateChatScreen(
    uiState: NewChatUiState,
    onNewGroupClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    navigateToChat: (UserItem) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = "New Message",
                onBackPressed = onBackPressed,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .animateContentSize(),
        ) {
            CheersSearchBar(
                searchInput = uiState.query,
                modifier = Modifier.padding(16.dp),
                onSearchInputChanged = onQueryChange,
                leadingIcon = { Text("To:") },
            )
            NewGroupButton(onNewGroupClick = onNewGroupClick)

            val users = uiState.users ?: return@Scaffold

            LazyColumn {
                items(
                    items = users,
                    key = { it.id },
                ) { user ->
                    UserItem(
                        userItem = user,
                        onClick = {
                            navigateToChat(user)
                        },
                        onStoryClick = {},
                    )
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
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
            )
        }
    }
}