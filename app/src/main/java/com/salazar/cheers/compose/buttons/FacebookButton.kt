package com.salazar.cheers.compose.buttons

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.R
import com.salazar.cheers.ui.theme.Roboto

@ExperimentalMaterialApi
@Composable
fun FacebookButton(
    text: String = "Continue with Facebook",
    loadingText: String = "Fetching data...",
    onClicked: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    Surface(
        onClick = { clicked = !clicked },
//        shape = shape,
//        border = BorderStroke(width = 1.dp, color = borderColor),
        color = Color(0xFF0091ea),
        elevation = 0.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
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
                painter = painterResource(id = R.drawable.ic_f_logo_rgb_white_1024),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (clicked) loadingText else text,
                color = Color.White,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold
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
private fun FacebookButtonPreview() {
    FacebookButton(
        text = "Login with Facebook",
        loadingText = "Creating Account...",
        onClicked = {}
    )
}