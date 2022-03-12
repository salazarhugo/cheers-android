package com.salazar.cheers.ui.main.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun BeverageScreen(
    onBackPressed: () -> Unit,
    onSelectBeverage: (Point) -> Unit,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context = context) }

    Scaffold(
        topBar = {
            ChooseOnMapAppBar(
                onBackPressed = onBackPressed,
                onSelectBeverage = onSelectBeverage,
                mapView = mapView,
            )
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.Place,
                "",
                modifier = Modifier.size(52.dp),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun ChooseOnMapAppBar(
    mapView: MapView,
    onBackPressed: () -> Unit,
    onSelectBeverage: (Point) -> Unit,
) {
    SmallTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        title = {
            Column {
                Text(
                    text = "Choose beverage",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto,
                    fontSize = 14.sp
                )
                Text(
                    text = "Stolichnaya",
                    fontSize = 14.sp
                )
            }
        },
        actions = {
            TextButton(onClick = {
                val center = mapView.getMapboxMap().cameraState.center
                onSelectBeverage(center)
            }) {
                Text("OK")
            }
        },
    )
}
