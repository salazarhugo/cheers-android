package com.salazar.cheers.ui.otherprofile

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto
import com.salazar.cheers.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OtherFollowersFollowing : Fragment() {

    @Inject
    lateinit var otherDetailViewModelFactory: OtherFollowersFollowingViewModel.OtherFollowersFollowingViewModelFactory

    private val viewModel: OtherFollowersFollowingViewModel by viewModels {
        OtherFollowersFollowingViewModel.provideFactory(otherDetailViewModelFactory, "username")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
                        "",
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
        val pages = if (uiState is FollowersFollowingUiState.HasFollowers)
            listOf(
                "Mutual",
                "${uiState.followers.size} followers",
                "${uiState.following.size} following",
                "Suggested"
            )
        else
            listOf("Mutual", "Followers", "Following", "Suggested")

        val initialPage = 2
        val pagerState = rememberPagerState(initialPage = initialPage)
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
                    text = { Text(title, style = MaterialTheme.typography.bodyMedium) },
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
            Column(modifier = Modifier.fillMaxSize()) {
                when (page) {
                    0 -> {
                        // TODO(Mutual friends)
//                        if (uiState is FollowersFollowingUiState.HasFollowers)
//                            Followers(followers = uiState.mutualFriends)
                    }
                    1 -> {
                        if (uiState is FollowersFollowingUiState.HasFollowers)
                            Followers(followers = uiState.followers)
                    }
                    2 -> {
                        if (uiState is FollowersFollowingUiState.HasFollowers)
                            Following(following = uiState.following)
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
                FollowerCard(follower)
            }
        }
    }

    @Composable
    fun Following(following: List<User>) {
        LazyColumn {
            items(following) { following ->
                FollowingCard(following)
            }
        }
    }

    @Composable
    fun FollowerCard(user: User) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
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
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                onClick = { /* TODO */ }
            ) {
                Text("Remove")
            }
        }
    }

    @Composable
    fun FollowingCard(user: User) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
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
            Row {
                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = { viewModel.unfollow(user.id) }
                ) {
                    Text("Following")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.MoreVert, null)
                }
            }
        }
    }

    @Composable
    fun SearchBar() {
        Box(
            modifier = Modifier.padding(15.dp),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                elevation = 0.dp,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {}
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
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                }),
                placeholder = { Text("Search") },
                trailingIcon = {
                    if (query.value.isNotBlank())
                        Icon(Icons.Filled.Close, null,
                            Modifier.clickable { query.value = "" })
                }
            )
        }
    }
}
