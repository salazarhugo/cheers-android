package com.salazar.cheers.core.ui

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.salazar.cheers.shared.util.LocalActivity

/**
 * Prevents a Compose screen from appearing in screenshots and video captures
 */
@Composable
fun NoScreenshot() {
    val activity = LocalActivity.current
    DisposableEffect(Unit) {
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}