package com.salazar.cheers.core.ui.components.card

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.Roboto

@Composable
fun CardComponent(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int? = null,
    supportingText: String? = null,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 14.sp,
            )
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = text,
                        fontFamily = Roboto,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        fontSize = 20.sp,
                    )
                    if (supportingText != null) {
                        Text(
                            text = supportingText,
                            fontFamily = Roboto,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp,
                        )
                    }
                }
                if (image != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = image),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@ComponentPreviews
@Composable
fun CardComponentPreview() {
    CheersPreview {
        CardComponent(
            modifier = Modifier.padding(16.dp),
            title = "Wind",
            text = "5",
            supportingText = "Calm",
            image = R.drawable.artboard_1logo_cheers_white,
        )
    }
}
