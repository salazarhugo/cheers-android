package com.salazar.cheers.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil
import kotlinx.coroutines.launch

class FollowersFollowingFragment : Fragment() {

    private val viewModel: FollowersFollowingViewModel by viewModels()
    private val args: FollowersFollowingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FollowersFollowingScreen()
            }
        }
    }

    @Composable
    fun Toolbar() {
        Column {
            SmallTopAppBar(
                title = {
                    Text(
                        args.username,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        findNavController().popBackStack()
                    }) {
                        Icon(Icons.Outlined.ArrowBack, "")
                    }
                },
            )
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun Tabs(uiState: FollowersFollowingUiState) {
        val pages = listOf("Followers", "Following")
        val pagerState = rememberPagerState()
        val scope = rememberCoroutineScope()

        TabRow(
            // Our selected tab is our current page
            selectedTabIndex = pagerState.currentPage,
            // Override the indicator, using the provided pagerTabIndicatorOffset modifier
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            },
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) {
            // Add tabs for all of our pages
            pages.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                        viewModel.toggle()
                    },
                )
            }
        }
        if (uiState.isLoading)
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = MaterialTheme.colorScheme.onBackground,
            )
        SearchBar()
        HorizontalPager(
            count = pages.size,
            state = pagerState,
        ) { page ->
            Column {
                when (uiState) {
                    is FollowersFollowingUiState.HasFollowers -> Followers(followers = uiState.followers)
                    is FollowersFollowingUiState.HasFollowing -> Followers(followers = uiState.following)
                    is FollowersFollowingUiState.NoUsers -> {
                        Text("No Users")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FollowersFollowingScreen() {
        val uiState = viewModel.uiState.collectAsState().value

        Scaffold(
            topBar = { Toolbar() }
        ) {
            Column {
                Tabs(uiState)
            }
        }
    }

    @Composable
    fun Followers(followers: List<User>) {
        LazyColumn {
            items(followers) { follower ->
                UserCard(follower)
            }
        }
    }

    @Composable
    fun UserCard(user: User) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val action =
                        FollowersFollowingFragmentDirections.actionFollowersFollowingFragmentToOtherProfileFragment(
                            user.id
                        )
                    findNavController().navigate(action)
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val photo = remember { mutableStateOf<Uri?>(null) }

                if (user.profilePicturePath.isNotBlank())
                    StorageUtil.pathToReference(user.profilePicturePath)?.downloadUrl?.addOnSuccessListener {
                        photo.value = it
                    }
                Image(
                    painter = rememberImagePainter(data = photo.value),
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
            OutlinedButton(onClick = {
                viewModel.unfollow(user.id)
            }) {
                Text("Remove")
            }
        }
    }

    @Composable
    fun SearchBar() {
        Card(
            elevation = 0.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(15.dp),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            val query = remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current

            TextField(
                value = query.value,
                leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                onValueChange = {
                    query.value = it
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                }),
                placeholder = { Text("Search") }
            )
        }
    }
}
