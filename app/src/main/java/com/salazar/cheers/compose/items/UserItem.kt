package com.salazar.cheers.compose.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cheers.type.UserOuterClass
import com.salazar.cheers.compose.Username
import com.salazar.cheers.compose.share.UserProfilePicture
import com.salazar.cheers.data.db.entities.UserItem


@Composable
fun UserItem(
    userItem: UserItem,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onStoryClick: (String) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(userItem.username) }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserProfilePicture(
                picture = userItem.picture,
                storyState = userItem.story_state,
                onClick = {
                    if (userItem.story_state == UserOuterClass.StoryState.EMPTY)
                        onClick(userItem.username)
                    else
                        onStoryClick(userItem.username)
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (userItem.name.isNotBlank())
                    Text(text = userItem.name, style = MaterialTheme.typography.bodyMedium)
                Username(
                    username = userItem.username,
                    verified = userItem.verified,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
        }
        content()
    }
}
