package com.salazar.cheers.ui.sheets

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable

@Serializable
data class DeletePostDialog(
    val postID: String
)

fun NavController.navigateToDeletePostDialog(postID: String) {
    navigate(DeletePostDialog(postID = postID))
}

fun NavGraphBuilder.deletePostDialog(
    onBackPressed: () -> Unit,
) {
    dialog<DeletePostDialog> {
        DeletePostDialog(
            onDismiss = onBackPressed,
        )
    }
}