package com.salazar.cheers.ui.auth.signin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.share.ErrorMessage
import com.salazar.cheers.ui.auth.signup.SignUpUiState

@Composable
fun CreateAccountScreen(
    uiState: SignUpUiState,
    onPasswordChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onSignUp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Sign up as ${uiState.username}?", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("You can't change it later")
            Spacer(Modifier.height(32.dp))
            SignUpButton(
                uiState = uiState,
                onSignUp = onSignUp,
            )
            ErrorMessage(
                errorMessage = uiState.errorMessage,
                paddingValues = PaddingValues(vertical = 8.dp)
            )
        }

        val text = buildAnnotatedString {
            append("By clicking Next, you agree to our ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Terms. ")
            }
            append("Learn how we collect, use and share your data in our ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Data Policy.")
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun SignUpButton(
    uiState: SignUpUiState,
    onSignUp: () -> Unit,
) {
    Button(
        shape = RoundedCornerShape(8.dp),
        onClick = { onSignUp() },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !uiState.isLoading,
    ) {
        if (uiState.isLoading)
            CircularProgressIndicator(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = 1.dp
            )
        else
            Text(text = "Sign up")
    }
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChanged: (String) -> Unit,
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    TextField(
        value = password,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        onValueChange = {
            onPasswordChanged(it)
        },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onSearch = {
        }),
        placeholder = { Text("Password") },
        trailingIcon = {
            val image = if (passwordVisibility)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(imageVector = image, "")
            }
        }
    )
}

@Composable
fun EmailTextField(
    email: String,
    onEmailChanged: (String) -> Unit,
) {
    TextField(
        value = email,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
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
//            if (uiState.isAvailable == true)
//                Icon(imageVector = Icons.Default.Check, "")
//            else if (uiState.email.isNotEmpty())
//                IconButton(onClick = { onEmailChanged("") }) {
//                    Icon(imageVector = Icons.Default.Close, "")
//                }
        },
//        isError = ,
    )
}
