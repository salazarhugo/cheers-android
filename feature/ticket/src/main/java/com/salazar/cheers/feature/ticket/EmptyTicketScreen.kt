package com.salazar.cheers.feature.ticket

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.message.MessageComponent

@Composable
fun EmptyTicketScreen(
    onBackPressed: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        MessageComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            title = "Hmm... Seems like you have no tickets",
            primaryButtonText = "Browse parties",
            onPrimaryButtonClick = onBackPressed,
        )
    }
}

@ScreenPreviews
@Composable
fun EmptyTicketScreenPreview() {
    CheersPreview {
        EmptyTicketScreen()
    }
}
