package com.salazar.cheers.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R

@Preview(showBackground = true)
@Composable
fun UsernamePreview() {
    Username(username = "hugolsalazar", verified = true)
}

@Composable
fun Username(
    username: String,
    verified: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = username,
            style = textStyle,
            color = color,
            overflow = TextOverflow.Ellipsis,
        )
        if (verified) {
            Spacer(Modifier.width(4.dp))
            Image(
                painterResource(R.drawable.ic_verified),
                contentDescription = null,
                modifier = Modifier.size(textStyle.fontSize.value.dp),
            )
        }
    }
}