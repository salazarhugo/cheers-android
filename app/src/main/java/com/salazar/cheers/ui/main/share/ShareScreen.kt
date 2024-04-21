package com.salazar.cheers.ui.main.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.CheersOutlinedButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.feature.create_post.CaptionSection

@Composable
fun ShareScreen(
    uiState: ShareUiState,
    onShareUIAction: (ShareUIAction) -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
        Column(
            modifier = Modifier.statusBarsPadding(),
        ) {
            ShareHeader(
                party = uiState.party,
                message = uiState.message,
                onMessageChange = { onShareUIAction(ShareUIAction.OnMessageChange(it)) },
            )
            UserList(
                users = uiState.users,
            )
        }
    }
}

@Composable
fun ShareHeader(
    party: Party?,
    message: String,
    onMessageChange: (String) -> Unit,
) {
    CaptionSection(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        caption = message,
        onCaptionChanged = onMessageChange,
    )
}

@Composable
fun UserList(
    users: List<UserItem>
) {
    LazyColumn() {
        items(users) {
            UserItem(
                userItem = it,
                onClick = {},
                content = {
                    CheersOutlinedButton(onClick = {}) {
                        Text("Send")
                    }
                }
            )
        }
    }
}
