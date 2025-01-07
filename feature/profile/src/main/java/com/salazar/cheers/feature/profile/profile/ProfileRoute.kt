package com.salazar.cheers.feature.profile.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.PartyID
import com.salazar.cheers.core.util.Utils.shareToSnapchat
import com.salazar.cheers.feature.profile.ProfileSheetUIAction
import kotlinx.coroutines.launch

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToFriendList: () -> Unit,
    navigateToPostDetails: (String) -> Unit,
    navigateToPostMore: (String) -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToMapPostHistory: () -> Unit,
    navigateToCheerscode: () -> Unit,
    navigateToNfc: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToParty: (PartyID) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    ProfileScreen(
        uiState = uiState,
        navigateToSignIn = navigateToSignIn,
        navigateToSignUp = navigateToSignUp,
        onProfileUIAction = { action ->
            when (action) {
                ProfileUIAction.OnBackPressed -> navigateBack()
                ProfileUIAction.OnEditProfileClick -> navigateToEditProfile()
                ProfileUIAction.OnSwipeRefresh -> viewModel.onSwipeRefresh()
                ProfileUIAction.OnFriendListClick -> navigateToFriendList()
                is ProfileUIAction.OnPostDetailsClick -> navigateToPostDetails(action.postID)
                is ProfileUIAction.OnUserClick -> navigateToOtherProfile(action.userID)
                is ProfileUIAction.OnPostMoreClick -> navigateToPostMore(action.postID)
                is ProfileUIAction.OnPartyClick -> navigateToParty(action.partyID)
                is ProfileUIAction.OnLinkClick -> {
                    var url = action.link
                    if (!url.startsWith("https://")) {
                        url = "https://$url"
                    }
                    uriHandler.openUri(url)
                }
            }
        },
        onProfileSheetUIAction = { action ->
            when (action) {
                is ProfileSheetUIAction.OnNfcClick -> navigateToNfc()
                is ProfileSheetUIAction.OnSettingsClick -> navigateToSettings()
                is ProfileSheetUIAction.OnCopyProfileClick -> {
//                        scope.launch {
//                            val link =
//                                FirebaseDynamicLinksUtil.createShortLink("u/$username").getOrNull()
//                                    ?: return@launch
//                            clipboardManager.setText(AnnotatedString(link))
//                        }
                    navigateBack()
                }

                is ProfileSheetUIAction.OnAddSnapchatFriends -> {
                    scope.launch {
                        context.shareToSnapchat("")
                    }
                }

                is ProfileSheetUIAction.OnPostHistoryClick -> navigateToMapPostHistory()
                ProfileSheetUIAction.OnQrCodeClick -> navigateToCheerscode()
            }
        },
    )
}