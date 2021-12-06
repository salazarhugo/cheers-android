package com.salazar.cheers.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.Roboto

@Composable
fun GoogleButton(
    text: String = "Sign in with Google",
    loadingText: String = "Fetching data...",
    onClicked: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    Surface(
        onClick = { clicked = !clicked },
//        shape = shape,
//        border = BorderStroke(width = 1.dp, color = borderColor),
        color = Color(0xFF4285F4),
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 1.dp,
                    end = 16.dp,
                    top = 1.dp,
                    bottom = 1.dp
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google Button",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(30.dp)
                )
            }
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

@Composable
@Preview
private fun GoogleButtonPreview() {
    GoogleButton(
        text = "Sign in with Google",
        loadingText = "Creating Account...",
        onClicked = {}
    )
}