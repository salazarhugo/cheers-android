package com.salazar.cheers.feature.chat.ui.screens.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cheers.type.UserOuterClass
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.core.ui.theme.BlueCheers
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.feature.chat.BuildConfig
import com.salazar.cheers.feature.chat.R

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
                        text = "Group Members",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = room.members.size.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            items(uiState.members, key = { it.id }) { user ->
                UserCardItem(
                    user = user,
                    isOwner = user.id == room.ownerId,
                ) { onUserClick(user.username) }
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
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = picture)
                    .apply(block = fun ImageRequest.Builder.() {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    }).build()
            ),
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentDescription = null,
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

@Composable
fun UserCardItem(
    user: UserOuterClass.UserItem,
    isOwner: Boolean = false,
    onUserClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick(user.username) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
//            UserProfilePicture(picture = user.picture)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.name.isNotBlank())
                    Text(text = user.name, style = MaterialTheme.typography.bodyMedium)
                Username(
                    username = user.username,
                    verified = user.verified,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
        }
        if (isOwner) {
            Text(
                text = "Owner",
                color = BlueCheers,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
