package com.salazar.cheers.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.R
import com.salazar.cheers.components.MyTopAppBar
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.CheersTheme

class EditProfileFragment : DialogFragment() {

    private val mainViewModel: MainViewModel by viewModels()
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
                CheersTheme() {
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
        val user = mainViewModel.user.observeAsState(User()).value

        Scaffold(
            topBar = { MyTopAppBar("Edit Profile", { dismiss() }, { dismiss() }) },
        ) {
            Column {
                EditProfileHeader(user)
                EditProfileBody(user)
            }
        }
    }

    @ExperimentalCoilApi
    @Composable
    fun EditProfileHeader(user: User) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(data = user.photoUrl),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(96.dp)
                    .border(BorderStroke(2.dp, Color.LightGray), CircleShape),
            )
            Text("Change profile photo")
        }

    }

    @Composable
    fun EditProfileBody(user: User) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            val items = listOf("Name", "Username", "Bio")

            val name = viewModel.name
            viewModel.onNameChanged(user.fullName)

            items.forEach { item ->
                TextField(
                    label = { Text(item) },
                    value = name.value,
                    onValueChange = {
                        viewModel.onNameChanged(it)
                    },
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}
