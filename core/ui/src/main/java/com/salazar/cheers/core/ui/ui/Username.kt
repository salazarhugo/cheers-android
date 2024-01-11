package com.salazar.cheers.core.ui.ui

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
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.common.ui.extensions.noRippleClickable


@Composable
fun Username(
    modifier: Modifier = Modifier,
    username: String,
    verified: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.noRippleClickable {
            onClick()
        },
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

@ComponentPreviews
@Composable
fun UsernamePreview() {
    CheersPreview {
        Username(
            username = "hugolsalazar",
            verified = true,
        )
    }
}
