package com.salazar.cheers.ui.settings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.SignInActivity
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.components.SwitchM3
import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.User
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    onBackPressed: () -> Unit
) {
    val settings = listOf("Theme", "Notifications", "About")

    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed) },
    ) {
        Column {
            Settings(settings = settings)
            SignOutButton(onSignOut = onSignOut)
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
fun Toolbar(
    onBackPressed: () -> Unit,
) {
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
            IconButton(onClick = onBackPressed) {
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
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        enabled = false,
    ) {
        Text("Delete account")
    }
}

@Composable
fun SignOutButton(onSignOut: () -> Unit) {
    OutlinedButton(
        onClick = onSignOut,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
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