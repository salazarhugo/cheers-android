package com.salazar.cheers.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.SignInActivity
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.SwitchM3
import com.salazar.cheers.ui.theme.Roboto
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor

class SettingsFragment : Fragment() {

//    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsScreen() {
        val settings = listOf("Theme", "Notifications", "About")

        Scaffold(
            topBar = { Toolbar() },
        ) {
            Column {
                Settings(settings = settings)
                SignOutButton()
                DeleteAccountButton()
            }
        }

    }

    @Composable
    fun Settings(settings: List<String>) {
        LazyColumn {
            items(settings) { setting ->
                Setting(setting)
                DividerM3()
            }
        }
    }

    @Composable
    fun Toolbar() {
        SmallTopAppBar(
            title = {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Roboto
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = { findNavController().popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "")
                }
            },
        )
    }

    @Composable
    fun DeleteAccountButton() {
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
        ) {
            Text("Delete account")
        }
    }

    @Composable
    fun SignOutButton() {
        OutlinedButton(
            onClick = { signOut() },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Logout")
        }
    }

    @Composable
    fun Setting(setting: String) {
        val checkedState = remember { mutableStateOf(true) }
        Row(
            modifier = Modifier
                .clickable { checkedState.value = !checkedState.value }
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = setting)
//                Text(text = "Subtitle", style = MaterialTheme.typography.labelMedium)
            }
            SwitchM3(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it }
            )
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(intentFor<SignInActivity>().newTask().clearTask())
    }
}
