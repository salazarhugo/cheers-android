package com.salazar.cheers.ui.otherprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.salazar.cheers.internal.Counter
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.profile.EditProfileViewModel
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.Neo4jUtil

class OtherProfileFragment : Fragment() {

    private val viewModel: EditProfileViewModel by viewModels()

    val args: OtherProfileFragmentArgs by navArgs()

    lateinit var otherUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                OtherProfileScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val otherUserId = args.otherUserId
        viewModel.getUser(otherUserId)
        otherUser = viewModel.user2.value
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OtherProfileScreen() {
//        val a =  other
        Scaffold(
            topBar = { Toolbar(otherUser)}
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Section1(otherUser)
                Section2(otherUser)
                HeaderButtons(otherUser)
            }
        }
    }

    @Composable
    fun Toolbar(otherUser: User) {
        SmallTopAppBar(
            title = { Text(otherUser.username, fontWeight = FontWeight.Bold, fontFamily = Roboto) },
            navigationIcon = {
                IconButton(onClick = { findNavController().popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "")
                }
            },
            actions = { },
        )
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
    fun Section1(otherUser: User) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(
                    data = otherUser.photoUrl,
                    builder = {
                        transformations(CircleCropTransformation())
                    }
                ),
                modifier = Modifier.size(70.dp),
                contentDescription = null,
            )
            Counters(otherUser)
        }
    }

    @Composable
    fun Counters(otherUser: User) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            val items = listOf(
                Counter("Posts", otherUser.posts),
                Counter("Followers", otherUser.followers),
                Counter("Following", otherUser.following),
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

    @Composable
    fun HeaderButtons(otherUser: User) {
        Row() {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { Neo4jUtil.followUser(otherUser.id)}
            ) {
                Text("Follow")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { /*TODO*/ }
            ) {
                Text("Message")
            }
        }
    }
}
