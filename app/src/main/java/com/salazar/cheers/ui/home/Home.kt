package com.salazar.cheers.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.R
import com.salazar.cheers.internal.Post
import com.salazar.cheers.ui.search.SearchFragmentDirections
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.Neo4jUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import java.util.*

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updatePosts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var refreshing by remember { mutableStateOf(false) }
                LaunchedEffect(refreshing) {
                    if (refreshing) {
                        delay(2000)
                        refreshing = false
                    }
                }
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = refreshing),
                    onRefresh = {
                        viewModel.updatePosts()
                        refreshing = true
                    }
                ) {
                    HomeScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen() {
        Scaffold(
            topBar = { MyAppBar() },
            floatingActionButton = {
                FloatingActionButton(onClick = { findNavController().navigate(R.id.addDialogFragment)}) {
                    Icon(Icons.Default.Edit, "")
                }
            }
        ) {
            Divider()
            val posts = viewModel.posts.value
            PostList(posts = posts)
        }
    }

    @Composable
    fun PostList(posts: List<Post>) {
        LazyColumn() {
            items(posts) { post ->
                Post(post)
            }
        }
    }

    @Composable
    fun Post(post: Post) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val liked = remember { mutableStateOf(false) }
            PostHeader(post)
            Divider()
            PostBody(post, liked)
            PostFooter(post, liked)
        }
    }

    @Composable
    fun PostHeader(post: Post) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp, 11.dp)
                .clickable {
                    val action = HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(post.userId)
                    findNavController().navigate(action)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(data = post.photoUrl),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .clip(CircleShape),
                )
                Spacer(Modifier.width(8.dp))
                Text( text = post.username, style = Typography.bodyMedium)
            }
            Icon(Icons.Default.MoreVert, "", modifier = Modifier.clickable {
                viewModel.deletePost(post.id)
            })
        }
    }

    @Composable
    fun PostBody(post: Post, liked: MutableState<Boolean>) {
        Image(
            painter = painterResource(id = R.drawable.a),
            contentDescription = "avatar",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            liked.value = !liked.value
                            viewModel.likePost(post.id)
                        },
                    )
                }
        )
    }

    @Composable
    fun PostFooterButtons(post: Post, liked: MutableState<Boolean>) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                val icon = if (liked.value) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder

                Icon(icon, "", modifier = Modifier.clickable {
                    viewModel.likePost(post.id)
                    liked.value = !liked.value
                })

                Icon(Icons.Outlined.ChatBubbleOutline, "")
                Icon(Icons.Outlined.Share, "")
            }
            Icon(Icons.Outlined.BookmarkBorder, "")
        }
    }

    @Composable
    fun PostFooter(post: Post, liked: MutableState<Boolean>) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            PostFooterButtons(post, liked)
            Text( "${post.likes} ${if (post.likes > 1) "likes" else "like" }", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(post.username)
                    }
                    append(" ")
                    append(post.caption)
                },
                style = Typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text( post.createdTime, style = Typography.labelSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                FilledTonalButton(
//                    modifier = Modifier.weight(1f),
//                    onClick = { /*TODO*/ }
//                ) {
//                    Text(text = "Like")
//                }
//                Spacer(Modifier.width(12.dp))
//                FilledTonalButton(
//                    modifier = Modifier.weight(1f),
//                    onClick = { /*TODO*/ }) {
//                    Text(text = "Commment")
//                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }

    @Composable
    fun MyAppBar() {
        SmallTopAppBar(
            modifier = Modifier.height(55.dp),
            title = {
                    Image(
                        painter = painterResource(id = R.drawable.cheers),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(8.dp),
                        contentDescription = "",
                    )
            },
            actions = {
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Localized description"
                    )
                }
            },
        )
    }

}
