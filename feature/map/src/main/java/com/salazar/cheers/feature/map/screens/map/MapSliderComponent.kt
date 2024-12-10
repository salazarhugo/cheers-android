package com.salazar.cheers.feature.map.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.feature.map.BuildConfig
import kotlin.math.abs

@Composable
fun MapSliderComponent(
    zoom: Double,
    modifier: Modifier = Modifier,
    onValueChange: (Double) -> Unit = {},
) {
    var initialZoom by remember { mutableDoubleStateOf(INITIAL_ZOOM) }

    if (BuildConfig.DEBUG) {
        Column {
            Text(
                text = zoom.toString(),
            )
            Text(
                text = initialZoom.toString(),
            )
        }
    }


    Box(
        modifier = modifier
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        // Prevent gesture propagation to underlying components
                        change.consume()

                        // Calculate the new zoom level
                        val zoomChange = -dragAmount / 150 // Adjust sensitivity
                        initialZoom = (initialZoom + zoomChange).coerceIn(1.0, 20.0)

                        // Update the camera's zoom level
                        if (abs(initialZoom - zoom) > 0.1) {
                            onValueChange(initialZoom)
                        }
                    },
                )
            }
            .width(100.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.onBackground)
                .clip(CircleShape)
                .width(4.dp)
                .fillMaxHeight()
        ) {

        }
    }
}

@ComponentPreviews
@Composable
private fun MapSliderComponentPreview() {
    CheersPreview {
        MapSliderComponent(
            zoom = 15.0,
            modifier = Modifier.padding(16.dp),
        )
    }
}