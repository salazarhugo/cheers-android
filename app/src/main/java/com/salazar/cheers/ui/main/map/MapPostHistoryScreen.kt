package com.salazar.cheers.ui.main.map

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.MapView
import com.salazar.cheers.ui.compose.utils.Permission
import kotlinx.coroutines.launch

@Composable
fun MapPostHistoryScreen(
    uiState: MapPostHistoryUiState,
    mapView: MapView,
    onMapReady: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.MyLocation, null)
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.padding(it),
        ) {
            Permission(Manifest.permission.ACCESS_FINE_LOCATION) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        onMapReady()
                        mapView
                    },
                ) { mapView ->
                    scope.launch {
                        uiState.posts?.forEach { post ->
                            launch {
                                addPostsAnnotation(
                                    post = post,
                                    mapView = mapView,
                                    onSelectPost = {},
                                )
                            }
                        }
                    }
                }
//                UiLayer(
//                    scope = this,
//                    uiState = uiState,
//                    mapView = mapView,
//                    onSelectPost = onSelectPost,
//                    onTogglePublic = onTogglePublic,
//                    isPublic = uiState.isPublic,
//                    onCreatePostClicked = onCreatePostClicked,
//                )
            }
        }
    }
}
