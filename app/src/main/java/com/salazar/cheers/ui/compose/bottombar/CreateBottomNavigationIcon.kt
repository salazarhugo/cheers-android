package com.salazar.cheers.ui.compose.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview

@Composable
fun CreateBottomNavigationIcon(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = when(isSelected) {
        true -> MaterialTheme.colorScheme.onBackground
        false -> MaterialTheme.colorScheme.background
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 8.dp)
            ,
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
//            tint = color,
        )
    }
}

@Preview
@Composable
private fun CreateBottomNavigationIconPreview() {
    CheersPreview {
        CreateBottomNavigationIcon(
            isSelected = false,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun CreateBottomNavigationIconPreview_Selected() {
    CheersPreview {
        CreateBottomNavigationIcon(
            isSelected = true,
            modifier = Modifier.padding(16.dp),
        )
    }
}
