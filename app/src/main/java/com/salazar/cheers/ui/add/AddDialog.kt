package com.salazar.cheers.ui.add

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.Typography
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import coil.compose.rememberImagePainter
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.util.Neo4jUtil


class AddDialogFragment : DialogFragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private val viewModel: AddPostDialogViewModel by viewModels()

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
                        AddDialogScreen()
                    }
                }
            }
        }
    }

    @Composable
    fun TopAppBar() {
        SmallTopAppBar(
            title = { Text("New post", fontWeight = FontWeight.Bold, fontFamily = Roboto) },
            navigationIcon = {
                IconButton(onClick = { dismiss() }) {
                    Icon(Icons.Default.Close, "")
                }
            },
            actions = {
                OutlinedButton(onClick = {
                    viewModel.addPost(Post(caption = viewModel.caption.value))
                    dismiss()
                }) {
                    Text("Save")
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddDialogScreen() {
        val user = mainViewModel.user.observeAsState(User()).value

        Scaffold(
            topBar = { TopAppBar() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CaptionSection(user = user)
                Divider()
                TagSection(user = user)
                Divider()
                LocationSection(user = user)
                Divider()
            }
        }
    }

    @Composable
    fun LocationSection(user: User) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(text = "Location", fontSize = 14.sp)
        }
    }

    @Composable
    fun TagSection(user: User) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(text = "Tag people", fontSize = 14.sp)
        }
    }

    @Composable
    fun CaptionSection(user: User) {
        Row(
            modifier = Modifier.padding(15.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberImagePainter(data = user.photoUrl),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(40.dp)
                    .border(BorderStroke(1.dp, Color.LightGray), CircleShape),
            )
            val caption = viewModel.caption

            TextField(
                value = caption.value,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth(),
                onValueChange = {
                        viewModel.onCaptionChanged(it)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                placeholder = {
                    Text(text = "Write a caption...", fontSize = 13.sp)
                }
//                keyboardActions = KeyboardActions(onSearch = {
//                    focusManager.clearFocus()
//                })
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    //            dismiss()
    companion object {
        private const val TAG = "AddDialogFragment"
    }
}

