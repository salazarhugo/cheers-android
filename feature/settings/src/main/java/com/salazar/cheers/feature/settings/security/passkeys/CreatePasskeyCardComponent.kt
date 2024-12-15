package com.salazar.cheers.feature.settings.security.passkeys

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun CreatePasskeyCardComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "Sign in faster next time",
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "You can sign in securely with your passkey using your fingerprint, face, or other screen lock method.",
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
            ) {
                Text(
                    text = "Create a passkey",
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun CreatePasskeyCardComponentPreview() {
    CheersPreview {
        CreatePasskeyCardComponent(
            modifier = Modifier.padding(16.dp),
            onClick = {},
        )
    }
}