package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.theme.StrongRed

@Composable
fun CheersBadgeBox(
    count: Int,
    content: @Composable () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        content()
        if (count > 0)
            Badge(
                modifier = Modifier.offset(y = (-14).dp, x = 14.dp),
                containerColor = StrongRed,
                contentColor = Color.White,
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
    }
}
