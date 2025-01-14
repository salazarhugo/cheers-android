package com.salazar.cheers.feature.create_post.addlocation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.feature.create_post.CreatePostViewModel

@Composable
fun CreatePostAddLocationRoute(
    navigateBack: () -> Unit,
    viewModel: CreatePostViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreatePostAddLocationScreen(
        onSelectLocation = { point, zoom ->
            viewModel.updateLocationPoint(point)
            viewModel.getLocationName(
                point.longitude(),
                point.latitude(),
                zoom,
            )
            navigateBack()
        },
        navigateBack = navigateBack,
    )
}
