@file:OptIn(ExperimentalMaterial3Api::class)

package com.salazar.cheers.feature.chat.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.avatar.AvatarComponent
import com.salazar.cheers.core.ui.extensions.noRippleClickable
import com.salazar.cheers.core.ui.ui.Username
import com.salazar.cheers.feature.chat.R

@Composable
fun DirectChatBar(
    name: String,
    verified: Boolean,
    picture: String?,
    isTyping: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onTitleClick: (username: String) -> Unit = { },
) {
    CheersAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        center = false,
        navigationIcon = {
            IconButton(onClick = onNavIconPressed) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .noRippleClickable {
                        onTitleClick(name)
                    }
            ) {
                AvatarComponent(
                    avatar = picture,
                    name = name,
                    modifier = Modifier.padding(3.dp),
                    size = 33.dp,
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    if (name.isNotBlank()) {
                        Username(
                            username = name,
                            verified = verified,
                        )
                    }
                    ChatTypingComponent(
                        isTyping = isTyping,
                        modifier = Modifier,
                    )
                }
            }
        },
        actions = {
            // Search icon
            Icon(
                imageVector = Icons.Outlined.Search,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = {})
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.search)
            )
            // Info icon
            Icon(
                imageVector = Icons.Outlined.Info,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = onInfoClick)
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.info)
            )
        }
    )
}

@ComponentPreviews
@Composable
private fun DirectChatBarPreview() {
    CheersPreview {
        DirectChatBar(
            name = "Yuliia Maiorova",
            verified = true,
            picture = "",
            isTyping = true,
        )
    }
}