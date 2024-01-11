package com.salazar.cheers.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.StoryState
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent


@Composable
fun UserItem(
    userItem: UserItem,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    onStoryClick: (String) -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(userItem.username) }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarComponent(
                avatar = userItem.picture,
                onClick = {
                    if (userItem.story_state == StoryState.EMPTY)
                        onClick(userItem.username)
                    else
                        onStoryClick(userItem.username)
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (userItem.name.isNotBlank()) {
                    Text(
                        text = userItem.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
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

@ComponentPreviews
@Composable
private fun UserItemPreview() {
    CheersPreview {
        UserItem(
            userItem = cheersUserItem,
            modifier = Modifier,
        )
    }
}
