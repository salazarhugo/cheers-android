package com.salazar.cheers.core.ui.components.post.mentions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.UserID
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.FriendButton
import com.salazar.cheers.core.ui.UserItem
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.text.BottomSheetTopBar

@Composable
fun MentionsBottomSheet(
    postID: String,
    viewModel: MentionsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
    onUserClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val users = uiState.users

    LaunchedEffect(Unit) {
        viewModel.listPostMentions(postID)
    }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
    ) {
        MentionsScreen(
            users = users,
            onUserClick = onUserClick,
        )
    }
}

@Composable
fun MentionsScreen(
    users: List<UserItem>,
    onUserClick: (UserID) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        BottomSheetTopBar(
            text = "In this post",
        )
        UserList(
            users = users,
            modifier = Modifier,
            onUserClick = onUserClick,
        )
    }
}

@Composable
fun UserList(
    users: List<UserItem>,
    modifier: Modifier = Modifier,
    onUserClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            items = users,
        ) { user ->
            UserItem(
                userItem = user,
                onClick = onUserClick,
            ) {
                FriendButton(
                    isFriend = user.friend,
                    requested = user.requested,
                    onClick = { /*TODO*/ }
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun MentionsBottomSheetPreview() {
    CheersPreview {
        MentionsBottomSheet(
            postID = "",
            modifier = Modifier,
            onDismissRequest = {},
            onUserClick = {},
        )
    }
}
