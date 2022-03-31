package com.salazar.cheers.ui.main.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.salazar.cheers.util.Utils.getActivity

fun createNdefMessage(): NdefMessage {
    val text = "Beam me up, Android!\n\n" +
            "Beam Time: " + System.currentTimeMillis()
    return NdefMessage(
        arrayOf(
            createMime("application/vnd.com.salazar.cheers", text.toByteArray())
        )
    )
}

@Composable
fun NfcScreen() {

    val context = LocalContext.current
    val nfc = remember { NfcAdapter.getDefaultAdapter(context.getActivity()) }

    LaunchedEffect(nfc) {
        nfc?.setNdefPushMessage(createNdefMessage(), context.getActivity())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Card(
            onClick = {},
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(56.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Rounded.Contactless,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Scan phone",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
