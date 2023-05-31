package com.salazar.cheers.auth.ui.register

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onClick = {})
}

@Composable
fun WelcomeScreen(
    onClick: () -> Unit,
) {
    Scaffold(
        bottomBar = { WelcomeBottomBar(onClick = onClick) },
    ) {
        it
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WelcomeScreenBody()
        }
    }
}

@Composable
fun WelcomeScreenBody() {
    Image(
        painter = rememberAsyncImagePainter(R.drawable.ic_artboard_1cheers_logo_svg),
        contentDescription = null,
        modifier = Modifier
            .padding(vertical = 32.dp)
            .size(100.dp)
    )
    Text(
        text = "Welcome.",
        style = MaterialTheme.typography.displayMedium,
    )
    Spacer(Modifier.height(16.dp))
}

@Composable
fun WelcomeBottomBar(
    onClick: () -> Unit,
) {
    Button(
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(52.dp),
    ) {
        Text(text = "Continue")
    }
}
