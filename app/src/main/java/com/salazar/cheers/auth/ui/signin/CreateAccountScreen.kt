package com.salazar.cheers.auth.ui.signin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.platform.LocalUriHandler
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
import com.salazar.cheers.ui.compose.share.ErrorMessage

@Composable
fun CreateAccountScreen(
    errorMessage: String?,
    username: String,
    acceptTerms: Boolean,
    isLoading: Boolean,
    onAcceptTermsChange: (Boolean) -> Unit,
    onSignUp: () -> Unit,
) {

    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Sign up as $username?", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("You can't change it later")
            Spacer(Modifier.height(64.dp))
            AcceptTerms(
                onAcceptTermsChange = onAcceptTermsChange,
                acceptTerms = acceptTerms,
                onOpenLink = {
                    uriHandler.openUri(it)
                })
            Spacer(Modifier.height(32.dp))
            ErrorMessage(errorMessage = errorMessage, paddingValues = PaddingValues(vertical = 16.dp))
            SignUpButton(
                acceptTerms = acceptTerms,
                isLoading = isLoading,
                onSignUp = onSignUp,
            )
        }

    }
}

@Composable
fun AcceptTerms(
    onAcceptTermsChange: (Boolean) -> Unit,
    acceptTerms: Boolean,
    onOpenLink: (String) -> Unit,
) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append("By clicking Sign Up, you agree to our ")
        }

        pushStringAnnotation(
            tag = "Terms",
            annotation = "https://cheers-a275e.web.app/terms-of-use"
        )
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("Terms. ")
        }
        pop()

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append("Learn how we collect, use and share your data in our ")
        }

        pushStringAnnotation(
            tag = "Data Policy",
            annotation = "https://cheers-a275e.web.app/privacy-policy"
        )
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("Data Policy.")
        }
        pop()
    }

    Row {
        Checkbox(checked = acceptTerms, onCheckedChange = onAcceptTermsChange)
        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "Data Policy", start = offset,
                    end = offset
                )
                    .firstOrNull()?.let { annotation ->
                        onOpenLink(annotation.item)
                    }
                annotatedText.getStringAnnotations(
                    tag = "Terms", start = offset,
                    end = offset
                )
                    .firstOrNull()?.let { annotation ->
                        onOpenLink(annotation.item)
                    }
            },
        )
    }
}

@Composable
fun SignUpButton(
    acceptTerms: Boolean,
    isLoading: Boolean,
    onSignUp: () -> Unit,
) {
    Button(
        shape = MaterialTheme.shapes.medium,
        onClick = { onSignUp() },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = acceptTerms && !isLoading,
    ) {
        if (isLoading)
            CircularProgressIndicator(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = 1.dp
            )
        else
            Text(text = "Sign Up")
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
            .clip(MaterialTheme.shapes.medium),
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
            .clip(MaterialTheme.shapes.medium),
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
