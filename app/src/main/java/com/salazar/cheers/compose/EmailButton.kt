package com.salazar.cheers.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.ui.theme.Roboto

@ExperimentalMaterialApi
@Composable
fun EmailButton(
    text: String = "Sign in with Email",
    loadingText: String = "Fetching data...",
    onClicked: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    Surface(
        onClick = { clicked = !clicked },
//        shape = shape,
//        border = BorderStroke(width = 1.dp, color = borderColor),
        color = Color(0xFF800000),
        elevation = 0.dp,
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 36.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Email,
                contentDescription = "Email Button",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = if (clicked) loadingText else text,
                color = Color.White,
                fontFamily = Roboto
            )
            if (clicked) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(16.dp)
                        .width(16.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                onClicked()
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview
private fun EmailButtonPreview() {
    EmailButton(
        text = "Sign in with Email",
        loadingText = "Creating Account...",
        onClicked = {}
    )
}