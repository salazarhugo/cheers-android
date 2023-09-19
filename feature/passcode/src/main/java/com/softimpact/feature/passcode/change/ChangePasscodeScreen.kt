package com.softimpact.feature.passcode.change

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.Toolbar
import com.softimpact.feature.passcode.create.pinSize
import com.softimpact.feature.passcode.share.PasscodeDial
import com.softimpact.feature.passcode.share.PasscodeInput

@Composable
fun ChangePasscodeScreen(
    modifier: Modifier = Modifier,
    uiState: ChangePasscodeUiState,
    passcode: String,
    errorMessage: String?,
    onSubmit: () -> Unit,
    onAddDigit: (Int) -> Unit,
    onRemoveLastDigit: () -> Unit,
    onBackPressed: () -> Unit,
    onFingerprintClick: () -> Unit,
) {
    if (passcode.length == pinSize) {
        LaunchedEffect(true) {
            onSubmit()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                title = "",
                onBackPressed = onBackPressed,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(top = it.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = com.salazar.cheers.core.ui.R.drawable.ic_cheers_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 40.dp,
                        top = 20.dp
                    )
                )

                val title = when (uiState.isConfirmationScreen) {
                    true -> "Re-enter your passcode"
                    false -> "Enter new passcode"
                }

                val subtitle = when (uiState.isConfirmationScreen) {
                    true -> "If you forget your passcode, you'll need to log out or reinstall the app."
                    false -> "Please enter any $pinSize digits that you will use to unlock your Cheers app"
                }

                AnimatedContent(targetState = title, label = "") {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }

                AnimatedContent(targetState = subtitle, label = "") {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )

                }

                Spacer(modifier = Modifier.height(22.dp))
                PasscodeInput(inputPin = passcode)
                Spacer(modifier = Modifier.height(50.dp))
            }
            if (errorMessage != null)
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
            PasscodeDial(
                onAddDigit = onAddDigit,
                onRemoveLastDigit = onRemoveLastDigit,
                onFingerprintClick = onFingerprintClick,
                biometricEnabled = false,
            )
        }
    }
}