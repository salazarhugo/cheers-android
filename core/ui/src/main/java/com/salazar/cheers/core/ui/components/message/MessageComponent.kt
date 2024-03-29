package com.salazar.cheers.core.ui.components.message

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.CheersTheme

@Composable
fun MessageComponent(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int = R.drawable.ds_ic_error_screen,
    subtitle: String? = null,
    primaryButtonText: String? = null,
    secondaryButtonText: String? = null,
    onPrimaryButtonClick: () -> Unit = {},
    onSecondaryButtonClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
        )
        Spacer(
            modifier = Modifier.height(24.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(16.dp),
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }

        if (primaryButtonText != null || secondaryButtonText != null) {
            Column(
                modifier = Modifier.padding(top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (primaryButtonText != null) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onPrimaryButtonClick,
                    ) {
                        Text(text = primaryButtonText)
                    }
                }

                if (secondaryButtonText != null) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onSecondaryButtonClick,
                    ) {
                        Text(text = secondaryButtonText)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageScreenComponent(
    title: String,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int = R.drawable.ds_ic_error_screen,
    subtitle: String? = null,
    primaryButtonText: String? = null,
    secondaryButtonText: String? = null,
    onPrimaryButtonClick: () -> Unit = {},
    onSecondaryButtonClick: () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        MessageComponent(
            title = title,
            modifier = modifier,
            image = image,
            subtitle = subtitle,
            primaryButtonText = primaryButtonText,
            secondaryButtonText = secondaryButtonText,
            onPrimaryButtonClick = onPrimaryButtonClick,
            onSecondaryButtonClick = onSecondaryButtonClick,
        )
    }
}

@ComponentPreviews
@Composable
private fun MessageComponentDarkPreview() {
    CheersPreview {
        MessageComponent(
            modifier = Modifier.padding(16.dp),
            title = stringResource(id = R.string.message_component_title),
            subtitle = stringResource(id = R.string.message_component_subtitle),
            primaryButtonText = "Sign in",
            secondaryButtonText = "Register",
        )
    }
}
