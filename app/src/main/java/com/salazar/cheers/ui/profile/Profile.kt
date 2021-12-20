package com.salazar.cheers.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.SignInActivity
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProfileScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Profile(user: User) {
        Scaffold(
            topBar = { Toolbar(user) }
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Section1(user = user)
                Section2(user = user)
                Spacer(Modifier.height(4.dp))
                Row {
                    OutlinedButton(
                        onClick = { findNavController().navigate(R.id.editProfileFragment) },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
//                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text("Edit Profile", color = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.BookmarkBorder, "")
                    }
                }
                OutlinedButton(
                    onClick = { signOut() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Logout")
                }
            }
        }
    }

    @Composable
    fun ProfileScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        when (uiState) {
            is ProfileUiState.Loading -> LoadingScreen()
            is ProfileUiState.HasUser -> Profile(uiState.user)
        }
    }

    @Composable
    fun Section2(user: User) {
        Column {
            Text(
                text = user.fullName,
                style = Typography.bodyMedium
            )
            Text(user.bio)
            Text(
                user.website,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }

    @Composable
    fun Toolbar(otherUser: User) {
        Column {
            SmallTopAppBar(
//                modifier = Modifier.height(55.dp),
//                backgroundColor = MaterialTheme.colorScheme.surface,
//                elevation = 0.dp,
                title = {
                    Text(
                        otherUser.username,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto,
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.AddBox, "")
                    }
                    IconButton(onClick = {
                        val action =
                            ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
                        findNavController().navigate(action)
                    }) {
                        Icon(Icons.Outlined.Settings, "")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.Menu, "")
                    }
                },
            )
            DividerM3()
        }
    }

    @Composable
    fun Section1(user: User) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth()
        ) {

            val photo = remember { mutableStateOf<Uri?>(null) }

            if (user.profilePicturePath.isNotBlank())
                StorageUtil.pathToReference(user.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                    photo.value = it
                }
            Image(
                painter = rememberImagePainter(
                    data = photo.value,
                    builder = {
                        transformations(CircleCropTransformation())
                        placeholder(R.drawable.default_profile_picture)
                    },
                ),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentDescription = null,
            )
            Counters(user)
            Spacer(Modifier.height(18.dp))
        }
    }

    @Composable
    fun Counters(user: User) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            val items = listOf(
                Counter("Posts", user.posts, null),
                Counter("Followers", user.followers, R.id.followersFollowingFragment),
                Counter("Following", user.following, R.id.followersFollowingFragment),
            )

            items.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        if (item.navId != null) {
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToFollowersFollowingFragment(
                                    user.username
                                )
                            findNavController().navigate(action)
                        }
                    }
                ) {
                    Text(
                        text = item.value.toString(),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    )
                    Text(text = item.name, fontSize = 14.sp, fontFamily = Roboto)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(intentFor<SignInActivity>().newTask().clearTask())
    }

}
