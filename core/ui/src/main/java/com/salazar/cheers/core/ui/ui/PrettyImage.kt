package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.SubcomposeAsyncImage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.components.image.InspectionAwareComponent
import com.salazar.cheers.core.ui.modifier.cheersShimmer

@Composable
fun PrettyImage(
    data: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.None,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    InspectionAwareComponent(
        modifier = modifier,
        inspectionModePainter = R.drawable.default_profile_picture,
    ) {
        SubcomposeAsyncImage(
            model = data,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            contentDescription = contentDescription,
            loading = {
                Box {
                    Spacer(
                        modifier = modifier.cheersShimmer(true),
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Couldn't load image. Tap to retry.",
                        color = Color.White,
                    )
                }
            },
        )
    }
}

@Composable
@Preview
private fun PrettyImagePreview() {
    CheersPreview {
        PrettyImage(
            data = "https://media.gqmagazine.fr/photos/616d7569d4bd52e104c66bca/16:9/w_2560%2Cc_limit/GettyImages-1216515595%2520(1).jpg"
        )
    }
}