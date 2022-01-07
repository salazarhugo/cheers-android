package com.salazar.cheers.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.R
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.MyTopAppBar
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : DialogFragment() {

    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DialogFullScreen)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.Theme_Cheers_Slide)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CheersTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        EditProfileScreen()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditProfileScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        if (uiState.done) {
            findNavController().navigate(R.id.profileFragment)
        }
        Scaffold(
            topBar = {
                MyTopAppBar("Edit Profile", { dismiss() }, {
                    viewModel.updateUser(uiState.user)
                })
            },
        ) {
            Column {
                if (uiState.isLoading)
                    LoadingScreen()
                else {
                    EditProfileHeader(uiState.user)
                    EditProfileBody(uiState.user)
                }
            }
        }
    }

    @ExperimentalCoilApi
    @Composable
    fun EditProfileHeader(user: User) {
        val openDialog = remember { mutableStateOf(false) }

        if (openDialog.value)
            EditProfilePhotoDialog(openDialog)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {

            val photo = if (viewModel.photoUri.value != null)
                viewModel.photoUri.value
            else
                user.profilePictureUrl

            Image(
                painter = rememberImagePainter(
                    data = photo,
                    builder = {
                        transformations(CircleCropTransformation())
                        error(R.drawable.default_profile_picture)
                    }
                ),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(96.dp)
                    .border(BorderStroke(2.dp, Color.LightGray), CircleShape)
                    .clickable {
                        Utils.openPhotoChooser(singleImageResultLauncher)
//                        openDialog.value = true
                    },
            )
            Spacer(Modifier.height(8.dp))
            Text("Change profile photo")
        }

    }

    private val singleImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    viewModel.photoUri.value = selectedImageUri
                }
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
    fun EditProfileBody(user: User) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Name",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = MaterialTheme.colorScheme.onBackground,
                ),
                value = user.fullName,
                onValueChange = {
                    viewModel.onNameChanged(it)
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Username",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = MaterialTheme.colorScheme.onBackground,
                ),
                value = user.username,
                onValueChange = {
                    viewModel.onBioChanged(it)
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Website",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = MaterialTheme.colorScheme.onBackground,
                ),
                value = user.website,
                onValueChange = {
                    viewModel.onWebsiteChanged(it)
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Bio", style = MaterialTheme.typography.labelMedium) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = MaterialTheme.colorScheme.onBackground,
                ),
                value = user.bio,
                onValueChange = {
                    viewModel.onBioChanged(it)
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}
