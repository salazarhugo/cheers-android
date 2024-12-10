package com.salazar.cheers.ui.main.party.create.basicinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview

@Composable
fun CreatePartyBasicInfoBottomBar(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(Modifier.width(1.dp))
            Button(
                onClick = onClick,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(12.dp),
                enabled = enabled,
            ) {
                Text(text = "Next")
            }
        }
    }
}

@Preview
@Composable
private fun CreatePartyBasicInfoBottomBarPreview() {
    CheersPreview {
        CreatePartyBasicInfoBottomBar(
            enabled = true,
            onClick = {},
        )
    }
}