package com.softimpact.feature.passcode.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.softimpact.commonlibrary.components.CrewImpactTopBar
import com.softimpact.commonlibrary.components.items.SettingItem
import com.softimpact.commonlibrary.components.lottie.LottieLoadingView


@Composable
fun PasscodeLockSettingScreen(
    uiState: PasscodeLockSettingUiState,
    onPasscodeLockSettingUIAction: (PasscodeLockSettingUIAction) -> Unit,
) {
    val biometricEnabled = uiState.biometricEnabled

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .systemBarsPadding(),
        topBar = {
            CrewImpactTopBar(
                text = stringResource(id = com.softimpact.commonlibrary.R.string.passcode_lock),
                onBackPressed = {
                    onPasscodeLockSettingUIAction(PasscodeLockSettingUIAction.OnBackPressed)
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieLoadingView(
                file = "pinlock.json",
                iterations = 100,
                modifier = Modifier
                    .size(200.dp)
                    .padding(vertical = 16.dp),
                onAnimationComplete = {},
            )
            SettingItem(
                title = stringResource(id = com.softimpact.commonlibrary.R.string.change_passcode),
                icon = Icons.Outlined.Lock,
                onClick = {
                    onPasscodeLockSettingUIAction(PasscodeLockSettingUIAction.OnChangePasscodeClick)
                },
            )
            SettingItem(
                title = stringResource(id = com.softimpact.commonlibrary.R.string.unlock_with_fingerprint),
                icon = Icons.Outlined.Fingerprint,
                onClick = {
                    onPasscodeLockSettingUIAction(PasscodeLockSettingUIAction.OnBiometricToggle)
                },
                content = {
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = {
                            onPasscodeLockSettingUIAction(PasscodeLockSettingUIAction.OnBiometricToggle)
                        }
                    )
                },
            )
            SettingItem(
                title = stringResource(id = com.softimpact.commonlibrary.R.string.turn_passcode_off),
                onClick = {
                    onPasscodeLockSettingUIAction(PasscodeLockSettingUIAction.OnTurnOffPasscodeClick)
                },
                color = MaterialTheme.colorScheme.error,
                content = {},
            )
        }
    }
}