package com.salazar.cheers.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.salazar.cheers.R
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.profile.EditProfileViewModel
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.Neo4jUtil

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.queryUsers("")
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
                FloatingActionButton(onClick = { /*TODO*/ }) {
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
            Text("Recent", style = Typography.titleSmall)
            Text("Suggested", style = Typography.titleSmall)
            Text("Results", style = Typography.titleSmall)
            val users = viewModel.conversation.value;
            UserList(users = users)
        }
    }

    @Composable
    fun UserList(users: List<User>) {
        LazyColumn() {
            items(users) { post ->
                UserCard(post)
                Divider()
            }
        }
    }

    @Composable
    fun UserCard(user: User) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val action = SearchFragmentDirections.actionSearchFragmentToOtherProfileFragment(user.id)
                    findNavController().navigate(action)
                }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(data = user.photoUrl),
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column() {
                    if (user.fullName.isNotBlank())
                        Text( text = user.fullName, style = Typography.bodyMedium)
                    Text( text = user.username, style = Typography.bodyMedium)
                }
            }
            Icon(Icons.Default.Close, "Fw")
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

            Row(
            ) {
                TextField(
                    value = query.value,
                    label = { Text(text = "Search", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, "Search icon") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth(),
                    onValueChange = {
                        query.value = it
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                    })
                )
//                TextButton(onClick = { /*TODO*/ }) {
//                    Text("Cancel")
//                }
            }
        }
    }

}
