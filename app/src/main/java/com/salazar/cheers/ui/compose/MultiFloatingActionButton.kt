package com.salazar.cheers.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MultiFloatingActionButton(
    fabIcon: ImageVector,
    items: List<MultiFabItem>,
    toState: MultiFabState,
    showLabels: Boolean = false,
    stateChanged: (fabstate: MultiFabState) -> Unit,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    val transition: Transition<MultiFabState> = updateTransition(targetState = toState, label = "")

    val scale: Float by transition.animateFloat(label = "Scale") { state ->
        if (state == MultiFabState.EXPANDED) 56f else 0f
    }

    val alpha: Float by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 50)
        }, label = "Alpha"
    ) { state ->
        if (state == MultiFabState.EXPANDED) 1f else 0f
    }

    val rotation: Float by transition.animateFloat(label = "Rotation") { state ->
        if (state == MultiFabState.EXPANDED) 45f else 0f
    }

    Column(horizontalAlignment = Alignment.End) {
        if (alpha != 0f)
            items.forEach { item ->
                MiniFabItem(item, alpha, scale, showLabels, onFabItemClicked)
                Spacer(modifier = Modifier.height(20.dp))
            }

        FloatingActionButton(onClick = {
            stateChanged(
                if (transition.currentState == MultiFabState.EXPANDED) {
                    MultiFabState.COLLAPSED
                } else MultiFabState.EXPANDED
            )
        }) {
            Icon(fabIcon, "", modifier = Modifier.rotate(rotation))
        }
    }
}

@Composable
private fun MiniFabItem(
    item: MultiFabItem,
    alpha: Float,
    scale: Float,
    showLabel: Boolean,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        if (showLabel) {
            Text(
                item.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .alpha(animateFloatAsState(targetValue = alpha).value)
//                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        SmallFloatingActionButton(
            onClick = { onFabItemClicked(item) },
            modifier = Modifier
                .alpha(animateFloatAsState(targetValue = alpha).value)
        ) {
            Icon(item.icon, null)
        }
    }
}