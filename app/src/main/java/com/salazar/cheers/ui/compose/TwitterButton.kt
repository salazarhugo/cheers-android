package com.salazar.cheers.ui.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.Roboto

@ExperimentalMaterialApi
@Composable
fun TwitterButton(
    text: String = "Login with Twitter",
    loadingText: String = "Fetching data...",
    onClicked: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    Surface(
        onClick = { clicked = !clicked },
//        shape = shape,
//        border = BorderStroke(width = 1.dp, color = borderColor),
        color = Color(0xFF00ACEE),
        elevation = 0.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 32.dp,
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
                painter = painterResource(id = R.drawable.ic_outline_face_24),
                contentDescription = "Twitter Button",
                tint = Color.Unspecified,
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
private fun TwitterButtonPreview() {
    TwitterButton(
        text = "Login with Twitter",
        loadingText = "Creating Account...",
        onClicked = {}
    )
}