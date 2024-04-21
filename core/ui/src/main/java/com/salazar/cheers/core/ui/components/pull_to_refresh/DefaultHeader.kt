package com.salazar.cheers.core.ui.components.pull_to_refresh

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
fun DefaultRefreshHeader(
    state: ActionState.RefreshingState,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF808080)
) {
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(state.componentStatus) {
        if (state.componentStatus == ActionComponentStatus.ReadyForAction) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Box(
        modifier = modifier
            .height(86.dp)
            .fillMaxWidth()
    ) {
        val agree = when (state.componentStatus) {
            ActionComponentStatus.ReadyForAction,
            ActionComponentStatus.ActionInProgress -> 90f
            else -> -90f
        }
        val rotation by animateFloatAsState(targetValue = agree, label = "")
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.componentStatus == ActionComponentStatus.ActionSuccess) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(2.dp),
                    tint = color,
                    contentDescription = null,
                )
            } else if (state.componentStatus == ActionComponentStatus.ActionInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp),
                    color = color,
                    strokeWidth = 2.dp
                )
            } else if (!state.componentStatus.isFinishing) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp)
                        .padding(2.dp)
                        .graphicsLayer {
                            this.rotationZ = rotation
                        },
                    colorFilter = ColorFilter.tint(color = color)
                )
            }
            var headerText by remember {
                mutableStateOf("")
            }
            val text = when (state.componentStatus) {
                ActionComponentStatus.IDLE -> stringResource(id = R.string.header_idle)
                ActionComponentStatus.ReadyForAction -> stringResource(id = R.string.header_pulling)
                ActionComponentStatus.ActionInProgress -> stringResource(id = R.string.header_refreshing)
                ActionComponentStatus.ActionSuccess -> stringResource(id = R.string.header_complete)
                ActionComponentStatus.ActionFailed -> stringResource(id = R.string.header_failed)
                else -> ""
            }
            if (headerText != text && text.isNotEmpty()) {
                headerText = text
            }
            if (state.componentStatus != ActionComponentStatus.ActionInProgress) {
//                Text(text = headerText, color = color)
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun DefaultRefreshHeaderPreview() {
    CheersPreview {
        DefaultRefreshHeader(
            state = rememberRefreshLayoutState().refreshingState,
            modifier = Modifier,
        )
    }
}

@ComponentPreviews
@Composable
private fun DefaultRefreshHeaderPreviewActionInProgress() {
    val state = rememberRefreshLayoutState().refreshingState
    state.updateComponentStatus(ActionComponentStatus.Dragging)
    CheersPreview {
        DefaultRefreshHeader(
            state = state,
            modifier = Modifier,
        )
    }
}
