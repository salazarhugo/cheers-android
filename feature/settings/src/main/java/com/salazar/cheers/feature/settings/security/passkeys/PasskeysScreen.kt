package com.salazar.cheers.feature.settings.security.passkeys

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Credential
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.feature.settings.security.PasskeyItem

@Composable
fun PasskeysScreen(
    uiState: PasskeysUiState,
    onBackPressed: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = onBackPressed,
                title = "Passkeys",
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
        ) {
            passkeys(
                credentials = uiState.passkeys,
            )
        }
    }
}

private fun LazyListScope.passkeys(
    credentials: List<Credential>,
) {
    if (credentials.isEmpty()) return

    item {
        SettingTitle(title = "Passkeys")
    }

    items(
        items = credentials,
    ) { passkey ->
        PasskeyItem(
            credential = passkey,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@ScreenPreviews
@Composable
private fun PasskeysScreenPreview() {
    CheersPreview {
        PasskeysScreen(
            uiState = PasskeysUiState(),
            onBackPressed = { /*TODO*/ },
        )
    }
}