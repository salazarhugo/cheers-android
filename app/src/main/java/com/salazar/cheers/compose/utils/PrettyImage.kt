package com.salazar.cheers.compose.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.SubcomposeAsyncImage
import com.salazar.cheers.compose.CircularProgressIndicatorM3

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
                CircularProgressIndicatorM3(Modifier.align(Alignment.Center))
            }
        },
        error = {
            Text(
                text = "Error while loading image",
                modifier = Modifier.align(Alignment.Center),
            )
        },
    )
}

@Composable
@Preview
private fun PrettyImagePreview() {
    PrettyImage(data = "https://media.gqmagazine.fr/photos/616d7569d4bd52e104c66bca/16:9/w_2560%2Cc_limit/GettyImages-1216515595%2520(1).jpg")
}