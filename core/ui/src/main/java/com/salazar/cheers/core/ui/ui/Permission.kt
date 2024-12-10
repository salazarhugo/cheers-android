@file:OptIn(ExperimentalPermissionsApi::class)

package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope

@Composable
fun RequestPermission(
    permission: String,
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {},
) {
    val permissionState = rememberPermissionState(
        permission = permission,
        onPermissionResult = {
            if (it) {
                onGranted()
            } else {
                onDenied()
            }
        }
    )

    when (permissionState.status) {
        PermissionStatus.Granted -> Unit
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
    onGranted: suspend CoroutineScope.() -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val permissionState = rememberPermissionState(
        permission
    )

    when (permissionState.status) {
        // If the  permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            LaunchedEffect(Unit, onGranted)
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

