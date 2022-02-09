package com.salazar.cheers.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

@Composable
fun PrettyImage(
    data: String?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.None,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    val painter = rememberImagePainter(
        data = data,
        builder = {
            crossfade(true)
        }
    )

    Box {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
        )

        when (painter.state) {
            is ImagePainter.State.Loading -> {
                CircularProgressIndicatorM3(Modifier.align(Alignment.Center))
            }
            is ImagePainter.State.Error -> {
                Text(
                    text = "Error while loading image",
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is ImagePainter.State.Empty -> {
                Text("Empty", modifier = Modifier.align(Alignment.Center))
            }
            is ImagePainter.State.Success -> {}
            else -> {}
        }
    }
}

@Composable
@Preview
private fun PrettyImagePreview() {
    PrettyImage(data = "https://media.gqmagazine.fr/photos/616d7569d4bd52e104c66bca/16:9/w_2560%2Cc_limit/GettyImages-1216515595%2520(1).jpg")
}