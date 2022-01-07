package com.salazar.cheers.ui.taguser

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.compose.rememberImagePainter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salazar.cheers.R
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.add.AddPostDialogViewModel
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TagUser : BottomSheetDialogFragment() {

    private val viewModel: TagUserViewModel by viewModels()
    private val sharedViewModel: AddPostDialogViewModel by activityViewModels()

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
                        val uiState = viewModel.uiState.collectAsState().value
                        TagUserScreen(uiState)
                    }
                }
            }
        }
    }

    @Composable
    fun TopAppBar() {
        SmallTopAppBar(
            title = { Text("Tag user", fontWeight = FontWeight.Bold, fontFamily = Roboto) },
            navigationIcon = {
                IconButton(onClick = { dismiss() }) {
                    Icon(Icons.Default.ArrowBack, "")
                }
            },
            actions = {
                TextButton(onClick = {
                    dismiss()
                }) {
                    Text("OK")
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TagUserScreen(uiState: TagUserUiState) {

        Scaffold(
            topBar = { TopAppBar() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DividerM3()
                when (uiState) {
                    is TagUserUiState.HasChannels -> {
                        ChipInput(uiState)
                        Users(users = uiState.users, sharedViewModel.selectedTagUsers)
                    }
                    is TagUserUiState.NoChannels -> Text("No Users")
                }
            }
        }
    }

    @Composable
    fun Users(users: List<User>, selectedUsers: List<User>) {
        LazyColumn {
            items(users) { user ->
                UserCard(user, selectedUsers.contains(user))
            }
        }
    }

    @Composable
    fun UserCard(user: User, selected: Boolean) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    sharedViewModel.selectTagUser(user)
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(data = user.profilePictureUrl),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    if (user.fullName.isNotBlank())
                        Text(text = user.fullName, style = Typography.bodyMedium)
                    Text(text = user.username, style = Typography.bodyMedium)
                }
            }
            androidx.compose.material.Checkbox(
                checked = selected,
                onCheckedChange = {
                    sharedViewModel.selectTagUser(user)
//                    viewModel.selectUser(user)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            )
        }
    }

    @Composable
    fun ChipInput(uiState: TagUserUiState.HasChannels) {
        val focusManager = LocalFocusManager.current

        val selectedUsers = sharedViewModel.selectedTagUsers
        ChipGroup(
            users = selectedUsers.map { it.username },
            onSelectedChanged = {
            }
        )
        TextField(
            value = uiState.searchInput,
            leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
//                viewModel.onSearchInputChanged(it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
            }),
            placeholder = { Text("Search") }
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}