package com.salazar.cheers.feature.premium.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersOutlinedButton
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun PremiumBottomBar(
    modifier: Modifier = Modifier,
    onSubscribeClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(16.dp),
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSubscribeClick,
        ) {
           Text(text = "Subscribe")
        }
    }
}

@ComponentPreviews
@Composable
private fun PremiumBottomBarPreview() {
    CheersPreview {
        PremiumBottomBar()
    }
}
