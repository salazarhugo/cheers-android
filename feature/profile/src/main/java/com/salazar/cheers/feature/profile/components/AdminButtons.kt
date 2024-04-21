package com.salazar.cheers.feature.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersOutlinedButton
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun AdminButtons(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        onClick = { /*TODO*/ },
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            CheersOutlinedButton(
                onClick = {},
            ) {
                Text("Verify User")
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun AdminButtonsPreview() {
    CheersPreview {
        AdminButtons(
            modifier = Modifier.padding(16.dp),
        )
    }
}