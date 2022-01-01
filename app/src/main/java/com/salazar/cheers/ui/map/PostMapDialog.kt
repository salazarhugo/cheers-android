package com.salazar.cheers.ui.map

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salazar.cheers.R
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.Username
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostMapDialog : BottomSheetDialogFragment() {

    private val mapViewModel: MapViewModel by activityViewModels()

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
//                        val uiState = viewModel.uiState.collectAsState().value
                        PostMapScreen()
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PostMapScreen() {
        val post = mapViewModel.uiState.collectAsState().value.selectedPost

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            DividerM3()
            if (post != null)
                Post(post)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun Post(post: Post) {
        val photo = remember { mutableStateOf<Uri?>(null) }

        val brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFD41668),
                Color(0xFFF9B85D),
            )
        )

        if (post.userPhotoUrl.isNotBlank())
            StorageUtil.pathToReference(post.userPhotoUrl)?.downloadUrl?.addOnSuccessListener {
                photo.value = it
            }
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = photo.value),
                contentDescription = "Profile image",
                modifier = Modifier
                    .border(1.2.dp, brush, CircleShape)
                    .size(33.dp)
                    .padding(3.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Username(username = post.username, verified = post.verified, textStyle = Typography.bodyMedium)
                if (post.locationName.isNotBlank())
                    Text(text = post.locationName, style = Typography.labelSmall)
            }
        }
        PostBody(post = post)
        Text(post.caption)
    }


    @Composable
    fun PostBody(post: Post) {
        val photo = remember { mutableStateOf<Uri?>(null) }

        if (post.photoPath.isNotBlank())
            StorageUtil.pathToReference(post.photoPath)?.downloadUrl?.addOnSuccessListener {
                photo.value = it
            }

        Image(
            painter = rememberImagePainter(data = photo.value),
            contentDescription = "avatar",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)// or 4/5f
                .fillMaxWidth()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}