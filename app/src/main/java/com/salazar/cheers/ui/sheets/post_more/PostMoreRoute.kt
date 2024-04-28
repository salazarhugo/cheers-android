package com.salazar.cheers.ui.sheets.post_more

import android.content.Intent
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.core.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.post.ui.PostMoreBottomSheet
import com.salazar.common.util.result.getOrNull
import kotlinx.coroutines.launch

/**
 * Stateful composable that displays the Navigation route for the Comments screen.
 *
 * @param postMoreViewModel that handles the business logic of this screen
 */
@Composable
fun PostMoreRoute(
    postMoreViewModel: PostMoreViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by postMoreViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val post = uiState.post
    if (post != null) {
        PostMoreBottomSheet(
            modifier = Modifier.navigationBarsPadding(),
            isAuthor = post.isAuthor,
            onDetails = {
                navActions.navigateToPostDetail(post.id)
            },
            onDelete = {
                navActions.navigateToDeletePostDialog(post.id)
            },
            onUnfollow = {}, //{ homeViewModel.unfollowUser(post.creator.username)},
            onReport = {},
            onShare = {
                scope.launch {
                    val link = FirebaseDynamicLinksUtil.createShortLink("p/${post.id}").getOrNull() ?: return@launch
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, link)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                    navActions.navigateBack()
                }
            },
            onBlock = {
                navActions.navigateBack()
            },
            onLinkClick = {
                scope.launch {
                    val link = FirebaseDynamicLinksUtil.createShortLink("p/${post.id}").getOrNull() ?: return@launch
                    clipboardManager.setText(AnnotatedString(link))
                    navActions.navigateBack()
                }
            }
        )
    }
}