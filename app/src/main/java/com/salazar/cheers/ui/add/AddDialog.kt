package com.salazar.cheers.ui.add

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.R
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.util.StorageUtil
import com.salazar.cheers.util.Utils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddDialogFragment : DialogFragment() {

    private val args: AddDialogFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by viewModels()
    private val viewModel: AddPostDialogViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DialogFullScreen)
        reverseGeocoding = MapboxSearchSdk.getReverseGeocodingSearchEngine()
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

    private lateinit var reverseGeocoding: ReverseGeocodingSearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    private val searchCallback = object : SearchCallback {

        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            if (results.isEmpty()) {
                Log.i("SearchApiExample", "No reverse geocoding results")
            } else {
                Log.i("SearchApiExample", "Reverse geocoding results: $results")
                viewModel.updateLocationResults(results)
                viewModel.updateLocation("On Pin")
            }
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Reverse geocoding error", e)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (args.location != null) {
            val options = ReverseGeoOptions(
                center = args.location!!,
            )
            searchRequestTask = reverseGeocoding.search(options, searchCallback)
        }

        return ComposeView(requireContext()).apply {
            setContent {
                CheersTheme {
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
                    Icon(Icons.Default.ArrowBack, "")
                }
            },
            actions = {
                Button(onClick = {
                    viewModel.uploadPost()
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
        val user = mainViewModel.user2.value

        Scaffold(
            topBar = { TopAppBar() },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (user == null)
                    LoadingScreen()
                else {
                    AddPhoto()
//                    if (viewModel.photoUri.value == null) DividerM3()
                    Spacer(Modifier.height(8.dp))
                    CaptionSection(user = user)
                    DividerM3()
                    TagSection()
                    DividerM3()
                    if (viewModel.selectedLocation.value != null)
                        SelectedLocation(location = viewModel.selectedLocation.value!!)
                    else
                        LocationSection()
                    DividerM3()
                    LocationResultsSection(results = viewModel.locationResults.value)
                }
            }
        }
    }

    @Composable
    fun AddPhoto() {
        if (viewModel.photoUri.value != null)
            return

        Row(
            modifier = Modifier.padding(15.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            FilledTonalButton(onClick = { openPhotoChooser() }) {
                Icon(Icons.Outlined.PhotoCamera, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add Photo")
            }
        }
    }

    @Composable
    fun LocationSection() {
        Row(
            modifier = Modifier
                .clickable {
                    findNavController().navigate(R.id.chooseOnMap)
                }
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Location", fontSize = 14.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.MyLocation, null)
                Text(text = viewModel.location.value, fontSize = 14.sp)
            }
        }
    }

    @Composable
    fun LocationResultsSection(results: List<SearchResult>) {
        LocationResult(results = results)
    }

    @Composable
    fun LocationResult(results: List<SearchResult>) {
        ChipGroup(
            users = results.map { it.name },
            onSelectedChanged = { name ->
                val location = results.find { it.name == name }
                if (location != null)
                    viewModel.selectLocation(location)
            },
            unselectedColor = MaterialTheme.colorScheme.outline,
        )
    }

    @Composable
    fun SelectedLocation(location: SearchResult) {
        Row(
            modifier = Modifier
                .clickable {
                    findNavController().navigate(R.id.chooseOnMap)
                }
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.tertiary)
                Text(text = location.name, fontSize = 14.sp)
            }
            Icon(
                Icons.Outlined.Close,
                null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable {
                    viewModel.unselectLocation()
                }
            )
        }
    }

    @Composable
    fun TagSection() {
        Row(
            modifier = Modifier
                .clickable {
                    findNavController().navigate(R.id.tagUser)
                }
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Tag people", fontSize = 14.sp)
            val tagUsers = viewModel.selectedTagUsers
            if (tagUsers.size == 1)
                Text(text = tagUsers[0].username, style = MaterialTheme.typography.labelLarge)
            if (tagUsers.size > 1)
                Text(text = "${tagUsers.size} people", style = MaterialTheme.typography.labelLarge)
        }
    }

    @Composable
    fun CaptionSection(user: User) {
        Row(
            modifier = Modifier.padding(15.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            val photo = remember { mutableStateOf<Uri?>(null) }

            if (user.profilePicturePath.isNotBlank())
                StorageUtil.pathToReference(user.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                    photo.value = it
                }
            Image(
                painter = rememberImagePainter(data = photo.value),
                contentDescription = "Profile image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .border(BorderStroke(1.dp, Color.LightGray), CircleShape),
                contentScale = ContentScale.Crop,
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
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                placeholder = {
                    Text(text = "Write a caption...", fontSize = 13.sp)
                },
                trailingIcon = {
                    if (viewModel.photoUri.value != null)
                        Image(
                            modifier = Modifier
                                .clickable(onClick = { openPhotoChooser() })
                                .padding(horizontal = 16.dp)
                                .size(50.dp),
                            painter = rememberImagePainter(data = viewModel.photoUri.value),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                }
//                keyboardActions = KeyboardActions(onSearch = {
//                    focusManager.clearFocus()
//                })
            )

        }
    }

    private fun openPhotoChooser() {
        Utils.openPhotoChooser(singleImageResultLauncher)
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

