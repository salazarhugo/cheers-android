package com.salazar.cheers.feature.edit_profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.User
import com.salazar.cheers.core.model.cheersUser
import com.salazar.cheers.core.model.coronaExtraDrink
import com.salazar.cheers.core.model.emptyDrink
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.MyTopAppBar
import com.salazar.cheers.core.ui.ProfileBannerAndAvatar
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.select_drink.SelectDrinkBottomSheet
import com.salazar.cheers.core.ui.ui.LoadingScreen
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    onBioChanged: (String) -> Unit = {},
    onNameChanged: (String) -> Unit = {},
    onUsernameChanged: (String) -> Unit = {},
    onWebsiteChanged: (String) -> Unit = {},
    onSelectImage: (Uri?) -> Unit = {},
    onSelectBanner: (Uri?) -> Unit = {},
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {},
    onDrinkClick: (Drink) -> Unit = {},
) {
    var showSelectDrinkSheet by remember { mutableStateOf(false) }
    val drinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val drinks = uiState.drinks

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Edit Profile",
                onPop = onDismiss,
                onSave = onSave,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else {
                EditProfileHeader(
                    user = uiState.user,
                    onSelectImage = onSelectImage,
                    onSelectBanner = onSelectBanner,
                    photoUri = uiState.profilePictureUri,
                    bannerUri = uiState.bannerUri,
                    onAddDrinkClick = {
                        scope.launch {
                            showSelectDrinkSheet = true
                            drinkSheetState.expand()
                        }
                    },
                )
                EditProfileBody(
                    user = uiState.user,
                    onBioChanged = onBioChanged,
                    onNameChanged = onNameChanged,
                    onWebsiteChanged = onWebsiteChanged,
                    onUsernameChange = onUsernameChanged,
                )
            }
        }
    }


    if (showSelectDrinkSheet) {
        SelectDrinkBottomSheet(
            drinks = drinks,
            sheetState = drinkSheetState,
            onClick = onDrinkClick,
            onDismiss = {
                scope.launch {
                    drinkSheetState.hide()
                }.invokeOnCompletion {
                    showSelectDrinkSheet = false
                }
            },
        )
    }
}

@Composable
fun EditProfileHeader(
    user: User,
    photoUri: Uri?,
    bannerUri: Uri?,
    onSelectImage: (Uri?) -> Unit,
    onSelectBanner: (Uri?) -> Unit,
    onAddDrinkClick: () -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> onSelectImage(uri) }
        )

    val bannerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> onSelectBanner(uri) }
        )

    if (openDialog.value)
        EditProfilePhotoDialog(openDialog)

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        val photo = photoUri?.toString() ?: user.picture
        val banner = bannerUri?.toString() ?: user.banner

        ProfileBannerAndAvatar(
            isEditable = true,
            modifier = Modifier.padding(16.dp),
            banner = banner,
            avatar = photo,
            onAvatarClick = {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onBannerClick = {
                bannerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            content = {
                EditProfileDrink(
                    drink = user.favouriteDrink,
                    onClick = onAddDrinkClick,
                )
            }
        )
    }

}

@Composable
fun EditProfilePhotoDialog(
    state: MutableState<Boolean>,
) {
    AlertDialog(
        dismissButton = {
            TextButton(onClick = { state.value = false }) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = { state.value = false }) {
                Text("Ok")
            }
        },
        title = { Text("dhad") },
        onDismissRequest = {
            state.value = false
        }
    )
}

@Composable
fun EditProfileBody(
    user: User,
    onBioChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onWebsiteChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = user.name,
            onValueChange = {
//                if (it.length <= NAME_MAX_CHAR)
                onNameChanged(it)
            },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Username",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = user.username,
            onValueChange = { onUsernameChange(it) },
            enabled = true,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = user.email,
            onValueChange = {},
            enabled = false,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Website",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = user.website,
            onValueChange = {
                if (it.length <= 500)
                    onWebsiteChanged(it)
            },
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            label = {
                Text("Bio", style = MaterialTheme.typography.labelMedium)
            },
            value = user.bio,
            onValueChange = {
                onBioChanged(it)
            },
        )
    }
}

@ScreenPreviews
@Composable
private fun EditProfileScreenPreview() {
    CheersPreview {
        EditProfileScreen(
            uiState = EditProfileUiState.HasPosts(
                user = cheersUser,
                bannerUri = null,
                done = false,
                errorMessages = emptyList(),
                isFollowing = false,
                isLoading = false,
                profilePictureUri = null,
                drinks = listOf(emptyDrink, coronaExtraDrink)
            ),
        )
    }
}