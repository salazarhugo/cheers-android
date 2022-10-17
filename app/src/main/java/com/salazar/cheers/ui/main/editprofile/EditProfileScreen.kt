package com.salazar.cheers.ui.main.editprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.compose.LoadingScreen
import com.salazar.cheers.compose.MyTopAppBar
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.Constants.BIO_MAX_CHAR
import com.salazar.cheers.util.Constants.NAME_MAX_CHAR

@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    onBioChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onWebsiteChanged: (String) -> Unit,
    onSelectImage: (Uri) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        topBar = {
            MyTopAppBar("Edit Profile", onDismiss, onSave)
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading)
                LoadingScreen()
            else {
                EditProfileHeader(
                    user = uiState.user,
                    onSelectImage = onSelectImage,
                    photoUri = uiState.profilePictureUri,
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
}

@Composable
fun EditProfileHeader(
    user: User,
    photoUri: Uri?,
    onSelectImage: (Uri) -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null)
                onSelectImage(it)
        }

    if (openDialog.value)
        EditProfilePhotoDialog(openDialog)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {

        val photo = photoUri ?: user.picture

        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = photo)
                    .apply(block = fun ImageRequest.Builder.() {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    }).build()
            ),
            contentDescription = "Profile image",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, Color.LightGray), CircleShape)
                .clickable { launcher.launch("image/*") },
        )
        Spacer(Modifier.height(8.dp))
        Text("Change profile photo")
    }

}

@Composable
fun EditProfilePhotoDialog(state: MutableState<Boolean>) {
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
        modifier = Modifier.padding(16.dp)
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    "Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = user.name,
            onValueChange = {
                if (it.length <= NAME_MAX_CHAR)
                    onNameChanged(it)
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    "Username",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = user.username,
            onValueChange = { onUsernameChange(it) },
            enabled = true,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    "Email",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = user.email,
            onValueChange = {},
            enabled = false,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    "Website",
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
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Bio", style = MaterialTheme.typography.labelMedium) },
//            colors = OutlinedTextFieldDefaults.textFieldColors(
//                backgroundColor = Color.Transparent,
//                textColor = MaterialTheme.colorScheme.onBackground,
//            ),
            value = user.bio,
            onValueChange = {
                if (it.length <= BIO_MAX_CHAR)
                    onBioChanged(it)
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
