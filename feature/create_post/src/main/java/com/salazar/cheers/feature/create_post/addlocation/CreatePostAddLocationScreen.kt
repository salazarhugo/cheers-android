package com.salazar.cheers.feature.create_post.addlocation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.feature.create_post.ChooseOnMapScreen

@Composable
fun CreatePostAddLocationScreen(
    modifier: Modifier = Modifier,
    onSelectLocation: (Point, Double) -> Unit,
    navigateBack: () -> Unit,
) {
    ChooseOnMapScreen(
        onBackPressed = navigateBack,
        onSelectLocation = onSelectLocation,
    )
}

@ScreenPreviews
@Composable
private fun CreatePostAddLocationScreenPreview() {
    CheersPreview {
        CreatePostAddLocationScreen(
            modifier = Modifier,
            onSelectLocation = { _, _ -> },
            navigateBack = {},
        )
    }
}