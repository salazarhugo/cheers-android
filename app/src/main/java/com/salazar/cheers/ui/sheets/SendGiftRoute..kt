package com.salazar.cheers.ui.sheets

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.salazar.cheers.R
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.theme.Green
import com.salazar.cheers.ui.theme.GreenSurface
import kotlinx.coroutines.delay

/**
 * Stateful composable that displays the Navigation route for the SendGift screen.
 *
 * @param sendGiftViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SendGiftRoute(
    sendGiftViewModel: SendGiftViewModel,
    navActions: CheersNavigationActions,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val uiState by sendGiftViewModel.uiState.collectAsState()

    val success = uiState.success

    when (success) {
        true -> {
            val y = bottomSheetNavigator.navigatorSheetState.offset.value
            SuccessSplashView(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -LocalDensity.current.run { y.toDp() } / 2),
                onFinish = {
                    navActions.navigateBack()
                }
            )
        }
        false -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text("FAILURE")
                Text(
                    text = uiState.errorMessage.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        else -> {
            if (uiState.isLoading) {
                val y = bottomSheetNavigator.navigatorSheetState.offset.value
                LoadingScreen(
                    modifier = Modifier.offset(y = -LocalDensity.current.run { y.toDp() } / 2),
                )
            } else
                SendGiftSheet(
                    name = uiState.receiver?.username ?: "",
                    onStickerClick = { sendGiftViewModel.sendGift() },
                    bottomSheetNavigator = bottomSheetNavigator,
                )
        }

    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SuccessSplashView(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_done)
        var atEnd by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            atEnd = !atEnd
            delay(1000)
            onFinish()
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier,
        ) {
            Icon(
                painter = rememberAnimatedVectorPainter(image, atEnd),
                contentDescription = "Your content description",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(GreenSurface)
                    .clickable {
                        atEnd = !atEnd
                    }
                    .padding(8.dp),
                tint = Green
            )
        }
    }
}