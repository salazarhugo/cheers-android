package com.salazar.cheers.feature.create_post.addpeople

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.core.model.cheersUserItem
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.feature.create_post.AddPeopleScreen

@Composable
fun CreatePostAddPeopleScreen(
    selectedUsers: List<UserItem>,
    modifier: Modifier = Modifier,
    onSelectPeople: (UserItem) -> Unit,
    navigateBack: () -> Unit,
) {
    AddPeopleScreen(
        selectedUsers = selectedUsers,
        onBackPressed = navigateBack,
        onSelectUser = onSelectPeople,
        onDone = {
            navigateBack()
        },
    )
}

@ScreenPreviews
@Composable
private fun CreatePostAddPeopleScreenPreview() {
    CheersPreview {
        CreatePostAddPeopleScreen(
            modifier = Modifier,
            onSelectPeople = {},
            navigateBack = {},
            selectedUsers = listOf(cheersUserItem, cheersUserItem)
        )
    }
}