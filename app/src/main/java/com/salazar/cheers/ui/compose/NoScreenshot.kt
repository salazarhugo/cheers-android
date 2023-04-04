package com.salazar.cheers.ui.compose

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.salazar.cheers.core.data.util.Utils.getActivity

/**
 * Prevents a Compose screen from appearing in screenshots and video captures
 */
@Composable
fun NoScreenshot() {
    val activity = requireNotNull(LocalContext.current.getActivity())
    DisposableEffect(Unit) {
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}