package com.salazar.cheers.ui.sheets

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable

const val POST_ID = "postID"
const val DELETE_POST_DIALOG_ROUTE = "delete_post_dialog_route/{$POST_ID}"

@Serializable
data class DeletePostDialog(
    val postID: String
)


fun NavController.navigateToDeletePostDialog(
    postID: String,
    navOptions: NavOptions? = null,
) = navigate("delete_post_dialog_route/$postID", navOptions)

fun NavGraphBuilder.deletePostDialog(
    onBackPressed: () -> Unit,
) {
//    dialog<DeletePostDialog> {
//        DeletePostDialog(
//            onDismiss = onBackPressed,
//        )
//    }
}