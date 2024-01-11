package com.salazar.cheers.feature.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.util.Utils.isEmailValid

@Composable
fun EmailScreen(
    email: String,
    isLoading: Boolean,
    onEmailChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter your email",
            style = MaterialTheme.typography.titleMedium,
        )
        EmailTextField(email = email, onEmailChanged = onEmailChanged)
        ButtonWithLoading(
            text = "Next",
            isLoading = isLoading,
            enabled = email.isEmailValid(),
            onClick = onNextClicked,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun EmailTextField(
    email: String,
    onEmailChanged: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TextField(
        value = email,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .focusRequester(focusRequester = focusRequester),
        onValueChange = { onEmailChanged(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onSearch = {
        }),
        placeholder = { Text("Email") },
        trailingIcon = {
            if (email.isEmailValid())
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            else if (email.isNotEmpty())
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
        },
    )
}

@ScreenPreviews
@Composable
private fun EmailScreenPreview() {
    CheersPreview {
       EmailScreen(
            email = "",
            isLoading = false,
            onEmailChanged = {},
            onNextClicked = {},
        )
    }
}