package com.salazar.cheers.ui.sheets

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.salazar.cheers.R
import com.salazar.cheers.core.ui.theme.Green
import com.salazar.cheers.core.ui.theme.GreenSurface
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import androidx.compose.material3.Divider
import kotlinx.coroutines.delay

/**
 * Stateful composable that displays the Navigation route for the SendGift screen.
 *
 * @param sendGiftViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SendGiftRoute(
    sendGiftViewModel: SendGiftViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val uiState by sendGiftViewModel.uiState.collectAsStateWithLifecycle()

    val success = uiState.success
    val username = uiState.receiver?.username

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(36.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.outline)
        )
        when (success) {
            true -> {
                //val y = bottomSheetNavigator.navigatorSheetState.offset.value
                val y = 0f
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
//                val y = bottomSheetNavigator.navigatorSheetState.offset.value
                val y = 0f
                FailureSplashView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -LocalDensity.current.run { y.toDp() } / 2),
                    onFinish = {
                        navActions.navigateBack()
                    },
                    errorMessage = uiState.errorMessage.toString(),
                    onRechargeClick = { navActions.navigateToRecharge() },
                )
            }
            else -> {
                if (uiState.isLoading) {
//                    val y = bottomSheetNavigator.navigatorSheetState.offset.value
                    val y = 0f
                    com.salazar.cheers.core.share.ui.LoadingScreen(
                        modifier = Modifier.offset(y = -LocalDensity.current.run { y.toDp() } / 2),
                    )
                } else if (uiState.isConfirmationScreen) {
                    val selectedSticker = uiState.selectedSticker
                    if (selectedSticker != null)
                        ConfirmTransactionScreen(
                            coins = selectedSticker.price,
                            username = username ?: ""
                        ) {
                            sendGiftViewModel.sendGift()
                        }
                } else
                    SendGiftSheet(
                        name = uiState.receiver?.username ?: "",
                        onStickerClick = sendGiftViewModel::selectSticker,
                        bottomSheetNavigator = bottomSheetNavigator,
                    )
            }

        }
    }
}

@Composable
fun ConfirmTransactionScreen(
    coins: Int,
    username: String,
    onConfirm: () -> Unit,
) {

    Text(
        text = "Send $coins ${if (coins > 1) "coins" else "coin"} to $username",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.onBackground,
    )
    Divider()
    Button(
        onClick = onConfirm,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
//                text = "Send $coins ${if (coins > 1) "coins" else "coin"} to $username"
            text = "Confirm",
        )
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun FailureSplashView(
    errorMessage: String,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
    onRechargeClick: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_fail)
        var atEnd by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            atEnd = !atEnd
        }


        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = rememberAnimatedVectorPainter(image, atEnd),
                    contentDescription = "Your content description",
                    modifier = Modifier
                        .size(64.dp)
//                    .clip(CircleShape)
//                    .background(GreenSurface)
                        .clickable {
                            atEnd = !atEnd
                        }
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(8.dp),
                )
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = onRechargeClick,
                ) {
                    Text("Recharge")
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SuccessSplashView(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
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