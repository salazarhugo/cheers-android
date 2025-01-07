package com.salazar.cheers.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.ui.Username


@Composable
fun UserItem(
    userItem: UserItem,
    modifier: Modifier = Modifier,
    onClick: (username: String) -> Unit = {},
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AvatarComponent(
                avatar = userItem.picture,
                name = userItem.name,
                username = userItem.username,
                onClick = {
                    onClick(userItem.username)
                }
            )
            Column {
                if (userItem.name.isNotBlank()) {
                    Text(
                        text = userItem.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (userItem.username.isNotBlank()) {
                    Username(
                        username = userItem.username,
                        verified = userItem.verified,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
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

@ComponentPreviews
@Composable
private fun UserItem2Preview() {
    CheersPreview {
        UserItem(
            userItem = cheersUserItem.copy(username = "", verified = false),
            modifier = Modifier,
        )
    }
}
