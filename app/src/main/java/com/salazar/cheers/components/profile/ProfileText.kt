package com.salazar.cheers.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.internal.User

@Composable
fun ProfileText(
    user: User,
    onWebsiteClicked: (String) -> Unit,
) {
    Column {
        Row {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.bodyMedium
            )
            if (user.verified) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "VIP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            user.bio,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
        )
        ClickableText(
            text = AnnotatedString(user.website),
            style = TextStyle(
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Normal
            ),
            onClick = { offset ->
                onWebsiteClicked(user.website)
            },
        )
    }
}
