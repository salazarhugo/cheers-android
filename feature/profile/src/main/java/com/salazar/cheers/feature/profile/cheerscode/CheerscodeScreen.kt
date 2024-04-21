package com.salazar.cheers.feature.profile.cheerscode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.qrcode.QrCodeComponent

@Composable
fun CheerscodeScreen(
    onBackPressed: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            CheerscodeTopBar(
                onBackPressed = onBackPressed,
            )
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QrCodeComponent(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .size(160.dp),
                    value = "",
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun CheerscodeScreenPreview() {
    CheersPreview {
        CheerscodeScreen()
    }
}
