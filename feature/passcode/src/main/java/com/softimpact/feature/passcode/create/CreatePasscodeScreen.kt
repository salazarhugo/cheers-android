package com.softimpact.feature.passcode.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.salazar.cheers.feature.passcode.R
import com.softimpact.feature.passcode.share.PasscodeDial
import com.softimpact.feature.passcode.share.PasscodeInput

const val pinSize = 4


@Composable
fun CreatePasscodeScreen(
    inputPin: String,
    onSubmit: () -> Unit,
    onAddDigit: (Int) -> Unit,
    onRemoveLastDigit: () -> Unit,
    onSkip: () -> Unit,
) {

    if (inputPin.length == pinSize) {
        LaunchedEffect(true) {
            onSubmit()
        }
    }

    Scaffold(
        bottomBar = {
            SetPinLockFooter(
                onSkip = onSkip,
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())
                .fillMaxSize()
                .background(Color.Black)
                .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
//                    .background()
                    .clip(RoundedCornerShape(22.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = stringResource(id = R.string.set_passcode),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(30.dp))
                PasscodeInput(inputPin = inputPin)
            }
            PasscodeDial(
                modifier = Modifier.align(Alignment.BottomCenter),
                onAddDigit = onAddDigit,
                onRemoveLastDigit = onRemoveLastDigit,
                biometricEnabled = false,
            )
        }
    }
}

@Composable
fun SetPinLockFooter(
    onSkip: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    ) {
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
        ) {
            Text(
                text = stringResource(id = R.string.skip).toUpperCase(Locale.current),
                color = Color.White,
            )
        }
    }
}
