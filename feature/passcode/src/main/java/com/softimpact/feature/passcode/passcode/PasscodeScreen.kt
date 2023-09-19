package com.softimpact.feature.passcode.passcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.feature.passcode.R
import com.softimpact.feature.passcode.create.pinSize
import com.softimpact.feature.passcode.share.PasscodeDial
import com.softimpact.feature.passcode.share.PasscodeInput

@Composable
fun PasscodeScreen(
    modifier: Modifier = Modifier,
    biometricEnabled: Boolean,
    inputPin: String,
    errorMessage: String?,
    onSubmit: () -> Unit,
    onAddDigit: (Int) -> Unit,
    onRemoveLastDigit: () -> Unit,
    onBackPressed: () -> Unit,
    banner: Int,
    onFingerprintClick: () -> Unit,
) {
    if (inputPin.length == pinSize) {
        LaunchedEffect(true) {
            onSubmit()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent,
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
                    painter = painterResource(id = banner),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 40.dp,
                        top = 20.dp
                    ).heightIn(max = 110.dp),
                )

                if (errorMessage != null)
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                else
                    Text(
                        text = "Enter your Cheers passcode",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )

                Spacer(modifier = Modifier.height(22.dp))
                PasscodeInput(inputPin = inputPin)
                Spacer(modifier = Modifier.height(50.dp))
            }
            PasscodeDial(
                onAddDigit = onAddDigit,
                onRemoveLastDigit = onRemoveLastDigit,
                onFingerprintClick = onFingerprintClick,
                biometricEnabled = biometricEnabled,
            )
        }
    }
}

@Preview
@Composable
fun PasscodeScreenPreview(
) {
    PasscodeScreen(
        inputPin = "",
        errorMessage = "",
        onSubmit = { /*TODO*/ },
        onAddDigit = {},
        onRemoveLastDigit = { /*TODO*/ },
        onFingerprintClick = {},
        onBackPressed = {},
        banner = R.drawable.crewimpactlogowhite,
        biometricEnabled = true,
    )
}