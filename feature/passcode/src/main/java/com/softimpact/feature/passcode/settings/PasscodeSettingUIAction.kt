package com.softimpact.feature.passcode.settings


sealed class PasscodeSettingUIAction {
    object OnBackPressed : PasscodeSettingUIAction()
    object OnTurnOffPasscodeClick : PasscodeSettingUIAction()
    object OnChangePasscodeClick : PasscodeSettingUIAction()
    object OnBiometricToggle : PasscodeSettingUIAction()
    object OnHideContentToggle: PasscodeSettingUIAction()
}

