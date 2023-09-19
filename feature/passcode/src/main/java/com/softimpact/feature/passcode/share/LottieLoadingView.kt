package com.softimpact.feature.passcode.share

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun LottieLoadingView(
    file: String,
    modifier: Modifier = Modifier,
    iterations: Int = 10,
    onAnimationComplete: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        delay(1000)
        onAnimationComplete()
    }
//    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(file))
//    val progress by animateLottieCompositionAsState(composition)
//
//    LottieAnimation(
//        composition = composition,
//        modifier = modifier.defaultMinSize(300.dp),
//        iterations = iterations
//    )
//
//    if (progress == 1.0f) {
//        LaunchedEffect(Unit) {
//            onAnimationComplete()
//        }
//    }
}

