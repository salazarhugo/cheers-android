package com.softimpact.feature.passcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Circle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.feature.passcode.R
import com.softimpact.feature.passcode.passcode.PinLockDial

const val pinSize = 6

@Preview
@Composable
fun PinLockScreenPreview(
) {
    PinLockScreen(
        inputPin = "",
        errorMessage = "",
        showSuccess = false,
        onSubmit = { /*TODO*/ },
        onAddDigit = {},
        onRemoveLastDigit = { /*TODO*/ },
        onAnimationComplete = { /*TODO*/ },
        onFingerprintClick = {},
        onBackPressed = {},
        banner = R.drawable.crewimpactlogowhite,
        biometricEnabled = true,
    )
}

@Composable
fun PinLockScreen(
    modifier: Modifier = Modifier,
    biometricEnabled: Boolean,
    inputPin: String,
    errorMessage: String?,
    showSuccess: Boolean,
    onSubmit: () -> Unit,
    onAddDigit: (Int) -> Unit,
    onRemoveLastDigit: () -> Unit,
    onAnimationComplete: () -> Unit,
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
                    )
                )

                if (errorMessage != null)
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                else
                    Text(
                        text = "Enter pin to unlock",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                    )

                Spacer(modifier = Modifier.height(22.dp))

                if (showSuccess) {
//                    LottieLoadingView(
//                        file = "success2.json",
//                        iterations = 1,
//                        modifier = Modifier.size(100.dp),
//                        onAnimationComplete = onAnimationComplete,
//                    )
                } else {
                    Row {
                        (0 until pinSize).forEach {
                            Icon(
                                imageVector = if (inputPin.length > it) Icons.Default.Circle else Icons.Outlined.Circle,
                                contentDescription = it.toString(),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(30.dp),
                                tint = Color.White,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
            PinLockDial(
                onAddDigit = onAddDigit,
                onRemoveLastDigit = onRemoveLastDigit,
                onFingerprintClick = onFingerprintClick,
                biometricEnabled = biometricEnabled,
            )
        }
    }
}