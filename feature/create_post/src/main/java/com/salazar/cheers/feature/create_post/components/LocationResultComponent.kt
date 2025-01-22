package com.salazar.cheers.feature.create_post.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.ChipGroup
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
internal fun LocationResultsComponent(
    results: List<String>,
    modifier: Modifier = Modifier,
    onLocationClick: (String) -> Unit = {},
) {
    if (results.isEmpty()) return

    LocationResult(
        results = results,
        modifier = modifier,
        onSelectLocation = onLocationClick,
    )
}

@Composable
private fun LocationResult(
    results: List<String>,
    modifier: Modifier = Modifier,
    onSelectLocation: (String) -> Unit = {},
) {
    ChipGroup(
        modifier = modifier,
        users = results,
        onSelectedChanged = { name ->
            val location = results.find { it == name }
            if (location != null) {
                onSelectLocation(location)
            }
        },
    )
}


@ComponentPreviews
@Composable
private fun SelectLocationComponent() {
    CheersPreview {
        LocationResultsComponent(
            results = listOf("Dublin", "Ireland", "Jameson Distillery Bow St."),
            modifier = Modifier.padding(16.dp),
        )
    }
}