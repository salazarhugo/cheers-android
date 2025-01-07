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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SettingsAccessibility
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.salazar.cheers.core.ui.ProfileHeaderCarousel
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.select_drink.SelectDrinkBottomSheet
import com.salazar.cheers.core.ui.item.SettingItem
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.CheersOutlinedTextField
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
    onSelectBanner: (List<Uri>) -> Unit,
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {},
    onDrinkClick: (Drink) -> Unit = {},
    onGenderClick: () -> Unit,
    onJobClick: () -> Unit,
    onDeleteBanner: (String) -> Unit,
) {
    var showSelectDrinkSheet by remember { mutableStateOf(false) }
    val drinkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val drinks = uiState.drinks

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Edit profile",
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
                    onAddDrinkClick = {
                        scope.launch {
                            showSelectDrinkSheet = true
                            drinkSheetState.expand()
                        }
                    },
                    onDeleteBanner = onDeleteBanner,
                )
                EditProfileBody(
                    user = uiState.user,
                    onBioChanged = onBioChanged,
                    onNameChanged = onNameChanged,
                    onWebsiteChanged = onWebsiteChanged,
                    onUsernameChange = onUsernameChanged,
                    onGenderClick = onGenderClick,
                    onJobClick = onJobClick,
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
    onSelectImage: (Uri?) -> Unit,
    onSelectBanner: (List<Uri>) -> Unit,
    onAddDrinkClick: () -> Unit,
    onDeleteBanner: (String) -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> onSelectImage(uri) }
        )

    val bannerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uri -> onSelectBanner(uri) }
        )

    if (openDialog.value)
        EditProfilePhotoDialog(openDialog)

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {

        val photo = photoUri?.toString() ?: user.picture

        ProfileHeaderCarousel(
            user = user.copy(
                banner = user.banner,
                picture = photo,
            ),
            editable = true,
            modifier = Modifier,
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
            onDeleteBannerClick = onDeleteBanner,
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
    onGenderClick: () -> Unit,
    onJobClick: () -> Unit,
) {
    val gender = user.gender

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CheersOutlinedTextField(
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
        CheersOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Username",
                )
            },
            value = user.username,
            onValueChange = { onUsernameChange(it) },
            readOnly = true,
            enabled = false,
            shape = MaterialTheme.shapes.medium,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Email",
                )
            },
            value = user.email,
            onValueChange = {},
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
//                disabledLabelColor = MaterialTheme.colorScheme.outline,
            ),
            shape = MaterialTheme.shapes.medium,
        )
        CheersOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Website",
                )
            },
            value = user.website,
            onValueChange = {
                if (it.length <= 500)
                    onWebsiteChanged(it)
            },
        )
        CheersOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            label = {
                Text("Bio")
            },
            value = user.bio,
            onValueChange = {
                onBioChanged(it)
            },
        )
    }
    SettingTitle(
        title = "About you",
    )
    SettingItem(
        title = "Work",
        icon = Icons.Outlined.Work,
        trailingContent = {
            if (user.jobTitle.isNotBlank()) {
                Text(
                    text = user.jobTitle + " at " + user.jobCompany,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        onClick = onJobClick,
    )
    SettingItem(
        title = "Education",
        icon = Icons.Outlined.School,
        trailingContent = {
            Text(
                text = user.education,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    )
    SettingItem(
        title = "Gender",
        icon = Icons.Outlined.PersonOutline,
        trailingContent = {
            if (gender != null) {
                Text(
                    text = gender.value,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        onClick = onGenderClick,
    )
    SettingTitle(
        title = "More about you",
    )
    SettingItem(
        title = "Height",
        icon = Icons.Outlined.Height,
        trailingContent = {
            Text(
                text = "",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    )
    SettingItem(
        title = "Religion",
        icon = Icons.Outlined.SettingsAccessibility,
        trailingContent = {
            Text(
                text = "",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    )
}

@ScreenPreviews
@Composable
private fun EditProfileScreenPreview() {
    CheersPreview {
        EditProfileScreen(
            uiState = EditProfileUiState.HasPosts(
                user = cheersUser,
                bannerUri = emptyList(),
                done = false,
                errorMessages = emptyList(),
                isFollowing = false,
                isLoading = false,
                profilePictureUri = null,
                drinks = listOf(emptyDrink, coronaExtraDrink)
            ),
            onGenderClick = {},
            onJobClick = {},
            onSelectBanner = {},
            onDeleteBanner = {},
        )
    }
}