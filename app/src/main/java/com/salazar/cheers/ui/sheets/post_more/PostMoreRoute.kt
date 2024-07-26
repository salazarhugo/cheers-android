package com.salazar.cheers.ui.sheets.post_more

import android.content.Intent
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.core.util.FirebaseDynamicLinksUtil
import com.salazar.cheers.core.ui.components.post.more.PostMoreBottomSheet
import com.salazar.cheers.core.ui.components.post.more.PostMoreViewModel
import com.salazar.cheers.shared.util.result.getOrNull
import kotlinx.coroutines.launch

@Composable
fun PostMoreRoute(
    postMoreViewModel: PostMoreViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by postMoreViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(true) }
    val drinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val post = uiState.post
    if (post != null) {
        if (showBottomSheet) {
            PostMoreBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        drinkSheetState.hide()
                    }.invokeOnCompletion {
                        showBottomSheet = false
                        navActions.navigateBack()
                    }
                },
                modifier = Modifier.navigationBarsPadding(),
                isAuthor = post.isAuthor,
                navigateToPostDetails = {
                    navActions.navigateToPostDetail(post.id)
                },
                navigateToDeleteDialog = {
                    navActions.navigateToDeletePostDialog(post.id)
                },
//                onShareClick = {
//                    scope.launch {
//                        val link =
//                            FirebaseDynamicLinksUtil.createShortLink("p/${post.id}").getOrNull()
//                                ?: return@launch
//                        val sendIntent: Intent = Intent().apply {
//                            action = Intent.ACTION_SEND
//                            putExtra(Intent.EXTRA_TEXT, link)
//                            type = "text/plain"
//                        }
//                        val shareIntent = Intent.createChooser(sendIntent, null)
//                        context.startActivity(shareIntent)
//                        navActions.navigateBack()
//                    }
//                },
//                onBlockClick = {
//                    navActions.navigateBack()
//                },
//                onLinkClick = {
//                    scope.launch {
//                        val link =
//                            FirebaseDynamicLinksUtil.createShortLink("p/${post.id}").getOrNull()
//                                ?: return@launch
//                        clipboardManager.setText(AnnotatedString(link))
//                        navActions.navigateBack()
//                    }
//                }
            )
        }
    }
}