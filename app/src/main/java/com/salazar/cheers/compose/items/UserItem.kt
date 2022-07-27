package com.salazar.cheers.compose.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.compose.Username
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.internal.StoryState
import com.salazar.cheers.internal.User


@Composable
fun UserItem(
    modifier: Modifier = Modifier,
    user: User,
    onClick: (String) -> Unit,
    onStoryClick: (String) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(user.username) }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserProfilePicture(
                avatar = user.profilePictureUrl,
                storyState = user.storyState,
                onClick = {
                    if (user.storyState == StoryState.EMPTY)
                        onClick(user.username)
                    else
                        onStoryClick(user.username)
                }
            )
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
        content()
    }
}
