package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.extensions.noRippleClickable
import kotlinx.coroutines.launch


@Composable
fun VerifiedComponent(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip {
                Text(
                    text = "Verified",
                )
            }
        },
        state = tooltipState
    ) {
        Image(
            painter = painterResource(R.drawable.ic_verified),
            contentDescription = "Verified icon",
            modifier = modifier
                .noRippleClickable {
                    scope.launch {
                        tooltipState.show()
                    }
                }
                .size(textStyle.fontSize.value.dp),
        )
    }
}

@ComponentPreviews
@Composable
fun VerifiedComponentPreview() {
    CheersPreview {
        VerifiedComponent(
            modifier = Modifier.padding(16.dp),
        )
    }
}
