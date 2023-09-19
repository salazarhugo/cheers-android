package com.salazar.cheers.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.geojson.Point
import com.salazar.cheers.core.share.ui.PrettyImage
import com.salazar.cheers.core.util.Utils.isDarkModeOn
import com.salazar.cheers.shared.data.AppConstants

@Composable
fun StaticMap(
    modifier: Modifier = Modifier,
    longitude: Double,
    latitude: Double,
    onMapClick: () -> Unit,
) {
    val context = LocalContext.current
    val style =
        if (context.isDarkModeOn())
            StaticMapCriteria.DARK_STYLE
        else
            StaticMapCriteria.LIGHT_STYLE

    val token = AppConstants.MAPBOX_ACCESS_TOKEN
    val staticImage = remember {
        MapboxStaticMap.builder()
            .accessToken(token)
            .styleId(style)
            .cameraPoint(Point.fromLngLat(longitude, latitude)) // Image's center point on map
            .staticMarkerAnnotations(
                listOf(
                    StaticMarkerAnnotation.builder().lnglat(Point.fromLngLat(longitude, latitude))
                        .build()
                )
            )
            .cameraZoom(13.0)
            .width(640)
            .height(640)
            .retina(true)
            .build()
    }

    val url = remember { staticImage.url().toString() }
    PrettyImage(
        modifier = modifier
            .clickable { onMapClick() },
        data = url,
    )
}

