package com.softimpact.feature.passcode.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ShareButton
import com.salazar.cheers.feature.passcode.R
import com.softimpact.feature.passcode.share.LottieLoadingView


@Composable
fun PasscodeOnBoarding(
    onSkip: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            DialogPinLockFooter(
                onSkip = onSkip,
                onNext = onNext,
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieLoadingView(
                    file = "pinlock.json",
                    iterations = 100,
                    modifier = Modifier
                        .size(200.dp),
                    onAnimationComplete = {},
                )
                Text(
                    text = stringResource(id = R.string.pin_lock),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.set_pin_description),
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun DialogPinLockFooter(
    onSkip: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShareButton(
            text = stringResource(id = R.string.enable_passcode),
            onClick = onNext,
            isLoading = false,
        )
        TextButton(
            onClick = onSkip,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.skip),
            )
        }
    }
}
