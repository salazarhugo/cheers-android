package com.salazar.cheers.ui.compose.utils

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.serialization.json.JsonNull.content

@Composable
fun RequestPermission(
    permission: String,
) {
    val permissionState = rememberPermissionState(
        permission
    )

    when (permissionState.status) {
        PermissionStatus.Granted -> {}
        is PermissionStatus.Denied -> {
            Column {
                if ((permissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                    Text("The  is important for this app. Please grant the permission.")
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Request permission")
                    }
                } else {
                    LaunchedEffect(Unit) {
                        permissionState.launchPermissionRequest()
                    }
                }
            }
        }
    }
}

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
                if ((permissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                    Text("The  is important for this app. Please grant the permission.")
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Request permission")
                    }
                } else {
                    LaunchedEffect(Unit) {
                        permissionState.launchPermissionRequest()
                    }
                }
            }
        }
    }
}

