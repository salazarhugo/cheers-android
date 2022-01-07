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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.R
import com.salazar.cheers.components.ChipGroup
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.components.SwitchM3
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.Roboto
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
                    AddPhotoOrVideo()
//                    DividerM3()
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
                    SwitchPreference(text = "Show on map") { viewModel.onShowOnMapChanged(it) }
                    DividerM3()
                    SwitchPreference(text = "Allow repost") {}
                    ShareButton()
                }
            }
        }
    }

    @Composable
    fun ShareButton() {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            DividerM3()
            Button(
                onClick = {
                    viewModel.uploadPost()
                    dismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Share")
            }
        }
    }

    @Composable
    fun SwitchPreference(
        text: String,
        onCheckedChange: (Boolean) -> Unit = {},
    ) {
        val checkedState = viewModel.showOnMap
        Row(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp))
            SwitchM3(
                checked = checkedState.value,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    fun AddPhotoOrVideo() {
        if (viewModel.mediaUri.value != null)
            return

        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            FilledTonalButton(onClick = { openPhotoVideoChooser() }) {
                Icon(Icons.Outlined.PhotoCamera, "")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add photo or video")
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Location",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.MyLocation, null)
                Text(
                    text = viewModel.location.value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )
            }
        }
    }

    @Composable
    fun LocationResultsSection(results: List<SearchResult>) {
        if (results.isNotEmpty())
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
            Text(
                text = "Tag people",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
            )
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
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val videoUri = viewModel.mediaUri.value

            if (videoUri != null && viewModel.postType.value == PostType.VIDEO)
                VideoPlayer(
                    uri = videoUri,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .height(120.dp)
                        .aspectRatio(9f / 16f)
                )
            else
                ProfilePicture(user)
            val caption = viewModel.caption

            TextField(
                value = caption.value,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 4.dp),
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
                    val photoUri = viewModel.mediaUri.value
                    if (photoUri != null) {
                        Image(
                            modifier = Modifier
                                .clickable(onClick = { openPhotoVideoChooser() })
                                .padding(horizontal = 16.dp)
                                .size(50.dp),
                            painter = rememberImagePainter(data = viewModel.mediaUri.value),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
//                keyboardActions = KeyboardActions(onSearch = {
//                    focusManager.clearFocus()
//                })
            )

        }
    }

    private fun openPhotoVideoChooser() {
        Utils.openPhotoVideoChooser(singleImageResultLauncher)
    }

    private val singleImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageOrVideoUri: Uri = data?.data ?: return@registerForActivityResult

                val type = data.resolveType(requireContext()) ?: ""
                if (type.startsWith("image")) {
                    viewModel.setPostImage(imageOrVideoUri)
                } else if (type.startsWith("video"))
                    viewModel.setPostVideo(imageOrVideoUri)
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

    @Composable
    fun VideoPlayer(
        uri: Uri,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current

        // Create media item
        val mediaItem = MediaItem.fromUri(uri)

        // Create the player
        val player = remember {
            ExoPlayer.Builder(context).build().apply {
                this.setMediaItem(mediaItem)
                this.prepare()
                this.playWhenReady = true
                this.repeatMode = Player.REPEAT_MODE_ALL
                this.volume = 0f
                this.videoScalingMode = VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
        }

        DisposableEffect(
            AndroidView(
                factory = {
                  PlayerView(context).apply {
                      this.player = player
                  }
                },
                modifier = modifier
            ) {
                it.useController = false
                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        ) {
            onDispose {
                player.release()
            }
        }
    }

    @Composable
    fun ProfilePicture(user: User) {
        Image(
            painter = rememberImagePainter(
                data = user.profilePictureUrl,
                builder = {
                    transformations(CircleCropTransformation())
                    error(R.drawable.default_profile_picture)
                },
            ),
            contentDescription = "Profile image",
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp),
            contentScale = ContentScale.Crop,
        )
    }

}

