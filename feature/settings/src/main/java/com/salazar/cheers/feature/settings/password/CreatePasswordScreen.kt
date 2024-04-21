package com.salazar.cheers.feature.settings.password

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.ui.ui.Toolbar

@Composable
fun CreatePasswordScreen(
    uiState: CreatePasswordUiState,
    onBackPressed: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onCreatePassword: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotBlank())
            snackBarHostState.showSnackbar(uiState.errorMessage)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Password") },
        bottomBar = {
            ShareButton(
                text = uiState.title,
                isLoading = uiState.isLoading,
                onClick = onCreatePassword,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(60.dp))
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = {
                    Text("Enter password")
                }
            )
        }
    }
}

@Composable
fun ShareButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        HorizontalDivider()
        ButtonWithLoading(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            text = text,
            isLoading = isLoading,
        )
    }
}
