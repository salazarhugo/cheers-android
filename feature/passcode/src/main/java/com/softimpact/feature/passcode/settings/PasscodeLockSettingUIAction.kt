package com.softimpact.feature.passcode.settings


sealed class PasscodeLockSettingUIAction {
    object OnBackPressed : PasscodeLockSettingUIAction()
    object OnTurnOffPasscodeClick : PasscodeLockSettingUIAction()
    object OnChangePasscodeClick : PasscodeLockSettingUIAction()
    object OnBiometricToggle : PasscodeLockSettingUIAction()
//    data class OnColumnClick(val column: Int) : PasscodeLockSettingUIAction()
}

