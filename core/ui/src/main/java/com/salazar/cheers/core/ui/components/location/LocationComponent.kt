package com.salazar.cheers.core.ui.components.location

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.extensions.noRippleClickable


@Composable
fun LocationComponent(
    city: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.noRippleClickable {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Place,
            contentDescription = "Drop down icon",
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = city,
            style = MaterialTheme.typography.titleMedium,

        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Drop down icon",
        )
    }
}

@Preview
@Composable
private fun LocationComponentPreview() {
    CheersPreview {
        LocationComponent(
            city = "Paris",
            onClick = {},
        )
    }
}