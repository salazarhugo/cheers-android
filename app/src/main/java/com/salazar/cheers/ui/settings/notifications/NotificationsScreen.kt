package com.salazar.cheers.ui.settings.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PrivateConnectivity
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.ui.compose.items.SettingItem
import com.salazar.cheers.ui.compose.items.SettingTitle

@Composable
fun NotificationsScreen(
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Notifications") },
    ) {
        it
        Column {
            NotificationsSection()
        }
    }
}

@Composable
fun NotificationsSection(
) {
    Column {
        SettingTitle(title = "Notifications for chats")
        SettingItem("Private Chats", Icons.Outlined.PrivateConnectivity, {})
        SettingItem("Groups", Icons.Outlined.Group, {})
    }
}


