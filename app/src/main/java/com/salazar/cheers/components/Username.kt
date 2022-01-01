package com.salazar.cheers.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.Roboto

@Preview(showBackground = true)
@Composable
fun UsernamePreview() {
    Username(username = "hugolsalazar", verified = true)
}

@Composable
fun Username(
    username: String,
    verified: Boolean = false,
    textStyle: TextStyle = TextStyle(fontFamily = Roboto),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = username,
            style = textStyle,
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