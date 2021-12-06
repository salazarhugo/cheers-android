package com.salazar.cheers.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.MainViewModel
import com.salazar.cheers.R
import com.salazar.cheers.SignInActivity
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.add.AddDialogFragment
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Neo4jUtil
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor

class ProfileFragment : Fragment() {

    private val viewModel: EditProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCurrentUser()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileScreen() {
        val user = viewModel.currentUser.value
        Scaffold(
            topBar = { Toolbar(user) }
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Section1(user = user)
                Section2(otherUser = user)
                Row() {
                    OutlinedButton(onClick = {
                        findNavController().navigate(R.id.editProfileFragment)
                    }) {
                        Text("Edit Profile")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.BookmarkBorder, "")
                    }
                }
                DividerM3()
                OutlinedButton(onClick = { signOut() }) {
                    Text("Logout")
                }
            }
        }
    }

    @Composable
    fun Section2(otherUser: User) {
        Column() {
            Text( text = "${otherUser.firstName} ${otherUser.lastName}", style = Typography.bodyMedium)
            Text(otherUser.bio)
            Text(otherUser.website)
        }
    }
    @Composable
    fun Toolbar(otherUser: User) {
        Column() {
            SmallTopAppBar(
                title = { Text(otherUser.username, fontWeight = FontWeight.Bold, fontFamily = Roboto) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.AddBox, "")
                    }
                    IconButton(onClick = {
                        val action = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
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
            Image(
                painter = rememberImagePainter(
                    data = user.photoUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                modifier = Modifier.size(70.dp),
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
                Counter("Posts", user.posts),
                Counter("Followers", user.followers),
                Counter("Following", user.following),
            )

            items.forEach { item->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = item.value.toString(), fontWeight = FontWeight.Bold)
                    Text(text = item.name)
                }
            }
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(intentFor<SignInActivity>().newTask().clearTask())
    }

}
