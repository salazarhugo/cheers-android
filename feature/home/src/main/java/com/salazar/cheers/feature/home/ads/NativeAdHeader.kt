package com.salazar.cheers.feature.home.ads

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.image.InspectionAwareComponent
import com.salazar.cheers.feature.home.R


@Composable
fun NativeAdHeader(
    headline: String,
    modifier: Modifier = Modifier,
    icon: Uri? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            InspectionAwareComponent(
                modifier = Modifier.size(24.dp),
                inspectionModePainter = R.drawable.ic_cheers_logo,
            ) {
                AsyncImage(
                    model = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = headline,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            Text(
                text = "Ad",
                modifier = Modifier
                    .sizeIn(minWidth = 16.dp, minHeight = 16.dp)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 15.sp,
            )
        }
    }
}


@ComponentPreviews
@Composable
private fun NativeAdHeaderPreview() {
    CheersPreview {
        NativeAdHeader(
            headline = "Starbucks",
            modifier = Modifier,
        )
    }
}
