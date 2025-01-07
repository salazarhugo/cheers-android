package com.salazar.cheers.feature.chat.ui.screens.room

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.feature.chat.BuildConfig

@Composable
fun RoomScreen(
    uiState: RoomUiState.HasRoom,
    onLeaveChat: () -> Unit,
    onBackPressed: () -> Unit,
    onUserClick: (String) -> Unit,
) {
    val room = uiState.room

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {},
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            item {
                GroupDetailsHeader(
                    roomId = room.id,
                    roomName = room.name,
                    picture = room.picture,
                )
                HeaderButtons(onAddMembers = { /*TODO*/ }) { }
            }

            item {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = pluralStringResource(
                            com.salazar.cheers.core.ui.R.plurals.members,
                            room.membersCount,
                            room.membersCount,
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            items(
                items = uiState.members,
            ) { user ->
                UserItem(
                    userItem = user,
                    onClick = {
                        onUserClick(user.username)
                    },
                )
            }

            item {
//                RedButton(
//                    text = "Leave Chat",
//                    onClick = onLeaveChat
//                )
            }
        }
    }
}


@Composable
fun GroupDetailsHeader(
    roomId: String,
    roomName: String,
    picture: String?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AvatarComponent(
            name = roomName,
            avatar = picture,
            size = 100.dp,
        )
        Text(
            text = roomName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )
        if (BuildConfig.DEBUG) {
            Text(
                text = roomId,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun HeaderButtons(
    onAddMembers: () -> Unit,
    onInviteViaLink: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        FilledTonalButton(onClick = onAddMembers) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
//                tint = BlueCheers,
            )
            Text("Add Members")
        }
        Spacer(Modifier.width(8.dp))
        FilledTonalButton(onClick = onInviteViaLink) {
            Icon(
                Icons.Default.Link,
                contentDescription = null,
//                tint = BlueCheers,
            )
            Spacer(Modifier.width(8.dp))
            Text("Invite Via Link")
        }
    }
}