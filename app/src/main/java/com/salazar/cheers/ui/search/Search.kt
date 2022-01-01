package com.salazar.cheers.ui.search

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
import androidx.compose.material.Card
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.mapbox.maps.extension.style.style
import com.salazar.cheers.components.Username
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.StorageUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen()
            }
        }
    }

    @Composable
    fun SearchScreen() {
        Column {
            MyAppBar()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyAppBar() {
        Scaffold(
            topBar = { SearchBar() },
            floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(Icons.Default.Search, "w")
                }
            }
        ) {
            SearchBody()
        }
    }

    @Composable
    private fun SearchBody() {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            val users = viewModel.resultUsers.value
            val recent = viewModel.recentUsers.collectAsState(emptyList()).value
            val recommendation = viewModel.userRecommendations.value
            if (users.isNullOrEmpty()) {
                Text("No results")
                Spacer(Modifier.height(32.dp))
            } else
                UserList(users = users)
            RecentUserList(recent = recent, recommendations = recommendation)
        }
    }

    @Composable
    fun UserList(users: List<User>) {
        LazyColumn {
            item {
                Text("Result")
            }
            items(users) { user ->
                UserCard(user)
            }
        }
    }

    @Composable
    fun RecentUserList(recent: List<RecentUser>, recommendations: List<User>) {
        LazyColumn {
            item {
                Text("Recent")
            }
            items(recent) { user ->
                RecentUserCard(user)
            }
            item {
                Text("Suggestions")
            }
            items(recommendations) { user ->
                UserCard(user)
            }
        }
    }

    @Composable
    fun RecentUserCard(user: RecentUser) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToOtherProfileFragment(username = user.username)
                    findNavController().navigate(action)
                }
                .padding(12.dp),
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
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    if (user.fullName.isNotBlank())
                        Text(text = user.fullName, style = Typography.bodyMedium)
                    Username(username = user.username, verified = user.verified, textStyle = Typography.bodyMedium)
                }
            }
            IconButton(onClick = { viewModel.deleteRecentUser(user) }) {
                Icon(Icons.Default.Close, null)
            }
        }
    }

    @Composable
    fun UserCard(user: User) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.insertRecentUser(user)
                    val action =
                        SearchFragmentDirections.actionSearchFragmentToOtherProfileFragment(username = user.username)
                    findNavController().navigate(action)
                }
                .padding(12.dp),
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
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    if (user.fullName.isNotBlank())
                        Text(text = user.fullName, style = Typography.bodyMedium)
                    Username(username = user.username, verified = user.verified, textStyle = Typography.bodyMedium)
//                    Text(text = user.username, style = Typography.bodyMedium)
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Close, null)
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

            val query = viewModel.query.value
            val focusManager = LocalFocusManager.current

            TextField(
                value = query,
                leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    viewModel.onQueryChanged(it)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    fontSize = 13.sp
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                }),
                placeholder = { Text("Search") },
                trailingIcon = {
                    if (query.isNotBlank())
                       Icon(Icons.Filled.Close, null,
                           Modifier.clickable { viewModel.onQueryChanged("") })
                }
            )
        }
    }

}
