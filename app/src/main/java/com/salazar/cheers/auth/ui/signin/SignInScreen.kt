package com.salazar.cheers.auth.ui.signin

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.R
import com.salazar.cheers.ui.compose.CircularProgressIndicatorM3
import com.salazar.cheers.ui.compose.DividerM3
import com.salazar.cheers.ui.compose.animations.AnimateVisibilityFade
import com.salazar.cheers.ui.compose.animations.AnimatedLogo
import com.salazar.cheers.ui.compose.buttons.GoogleButton
import com.salazar.cheers.ui.compose.share.ButtonWithLoading
import com.salazar.cheers.ui.compose.share.ErrorMessage
import com.salazar.cheers.ui.theme.Typography

@Preview
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        uiState = SignInUiState(isLoading = false),
        onSignInClick = { /*TODO*/ },
        signInWithGoogle = { /*TODO*/ },
        navigateToPhone = { /*TODO*/ },
        onPasswordLessChange = { /*TODO*/ },
        navigateToSignUp = { /*TODO*/ },
        onEmailChanged = {},
        onPasswordChanged = {},
    )
}

@Composable
fun SignInScreen(
    uiState: SignInUiState,
    onSignInClick: () -> Unit,
    signInWithGoogle: () -> Unit,
    navigateToPhone: () -> Unit,
    onPasswordLessChange: () -> Unit,
    navigateToSignUp: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val image = when (isSystemInDarkTheme()) {
        true -> R.drawable.cheers_logo_white
        false -> R.drawable.cheers_logo
    }

    val density = LocalDensity.current
    AnimatedVisibility(
        visibleState = state,
        enter = slideInHorizontally(
            initialOffsetX = { with(density) { +400.dp.roundToPx() } }
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutHorizontally() + fadeOut()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(22.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = null,
                    modifier = Modifier
                        .height(60.dp)
                )
                AnimatedLogo()
                Spacer(modifier = Modifier.height(30.dp))
                EmailTextField(uiState, onEmailChanged = onEmailChanged)
                AnimatedVisibility(
                    visible = !uiState.isPasswordless,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    PasswordTextField(uiState, onPasswordChanged = onPasswordChanged)
                }
                Spacer(Modifier.height(16.dp))
                LoginButton(
                    isLoading = uiState.isLoading,
                    signInWithEmailPassword = onSignInClick,
                )
                ErrorMessage(
                    errorMessage = uiState.errorMessage,
                    paddingValues = PaddingValues(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextDivider(dayString = "OR")
                val text =
                    if (uiState.isPasswordless) "Sign in with password" else "Sign in with email link"
                TextButton(onClick = onPasswordLessChange) {
                    Text(text = text)
                }
                Spacer(modifier = Modifier.height(16.dp))
                GoogleButton { signInWithGoogle() }
            }
            Footer(
//                modifier = Modifier.weight(1f),
                navigateToSignUp = navigateToSignUp,
            )
        }
    }
}

@Composable
fun PhoneButton(
    navigateToPhone: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        onClick = navigateToPhone,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF27814E),
        ),
        shape = MaterialTheme.shapes.small,
    ) {
        Icon(Icons.Default.Phone, "", tint = Color.White)
        Spacer(Modifier.width(12.dp))
        Text("Sign in with Phone", color = Color.White)
    }
}

@Composable
fun PasswordTextField(
    uiState: SignInUiState,
    onPasswordChanged: (String) -> Unit,
) {
    val password = uiState.password
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
        onValueChange = { onPasswordChanged(it) },
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
        enabled = !uiState.isLoading,
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
    uiState: SignInUiState,
    onEmailChanged: (String) -> Unit,
) {
    val email = uiState.email
    val focusManager = LocalFocusManager.current
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
            imeAction = ImeAction.Next
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        placeholder = { Text("Email address") },
        enabled = !uiState.isLoading,
    )
}

@Composable
fun LoginButton(
    isLoading: Boolean,
    signInWithEmailPassword: () -> Unit,
) {
    ButtonWithLoading(
        modifier = Modifier.fillMaxWidth(),
        text = "Sign In",
        isLoading = isLoading,
        onClick = signInWithEmailPassword,
        shape = MaterialTheme.shapes.medium,
    )
}

@Composable
fun Footer(
    modifier: Modifier = Modifier,
    navigateToSignUp: () -> Unit,
) {
    Column(
        modifier = modifier.clickable { navigateToSignUp() }
    ) {
        DividerM3()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                buildAnnotatedString {
                    append("Don't have an account? ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append("Sign up.")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun TextDivider(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        Line()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Line()
    }
}

@Composable
private fun RowScope.Line() {
    DividerM3(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        thickness = 2.dp,
    )
}