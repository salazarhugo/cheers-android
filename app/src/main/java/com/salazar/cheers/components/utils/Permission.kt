package com.salazar.cheers.components.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun Permission(
    permission: String,
    content: @Composable () -> Unit,
) {
    val permissionState = rememberPermissionState(
        permission
    )

    when (permissionState.status) {
        // If the  permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            content()
        }
        is PermissionStatus.Denied -> {
            Column {
                val textToShow =
                    if ((permissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                        "The  is important for this app. Please grant the permission."
                    } else {
                        "Camera permission required for this feature to be available. " +
                                "Please grant the permission"
                    }
                Text(textToShow)
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }
}

