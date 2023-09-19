package com.softimpact.feature.passcode.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.item.SettingItem
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.feature.passcode.R
import com.softimpact.feature.passcode.share.LottieLoadingView


@Composable
fun PasscodeSettingScreen(
    uiState: PasscodeLockSettingUiState,
    onPasscodeLockSettingUIAction: (PasscodeSettingUIAction) -> Unit,
) {
    val biometricEnabled = uiState.biometricEnabled
    val showContent = uiState.hideContent

    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.passcode_lock),
                onBackPressed = {
                    onPasscodeLockSettingUIAction(PasscodeSettingUIAction.OnBackPressed)
                },
            )
        }) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .verticalScroll(rememberScrollState()),
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
                title = stringResource(id = R.string.change_passcode),
                icon = Icons.Outlined.Lock,
                onClick = {
                    onPasscodeLockSettingUIAction(PasscodeSettingUIAction.OnChangePasscodeClick)
                },
            )
            SettingItem(
                title = stringResource(id = R.string.unlock_with_fingerprint),
                icon = Icons.Outlined.Fingerprint,
                onClick = {
                    onPasscodeLockSettingUIAction(PasscodeSettingUIAction.OnBiometricToggle)
                },
                trailingContent = {
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = {
                            onPasscodeLockSettingUIAction(PasscodeSettingUIAction.OnBiometricToggle)
                        }
                    )
                },
            )
            Spacer(modifier = Modifier.height(54.dp))
            SettingTitle(
                title = "App Content in Task Switcher",
            )
            SettingItem(
                title = stringResource(id = R.string.hide_content),
                onClick = {
                    onPasscodeLockSettingUIAction(PasscodeSettingUIAction.OnHideContentToggle)
                },
                trailingContent = {
                    Switch(
                        checked = showContent,
                        onCheckedChange = {
                            onPasscodeLockSettingUIAction(PasscodeSettingUIAction.OnHideContentToggle)
                        }
                    )
                },
            )
            SettingItem(
                title = stringResource(id = R.string.turn_passcode_off),
                onClick = {
                    onPasscodeLockSettingUIAction(PasscodeSettingUIAction.OnTurnOffPasscodeClick)
                },
                color = MaterialTheme.colorScheme.error,
                trailingContent = {},
            )
        }
    }
}