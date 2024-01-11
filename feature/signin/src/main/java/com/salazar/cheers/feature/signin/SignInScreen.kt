package com.salazar.cheers.feature.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.AnimatedLogo
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.ButtonWithLoading
import com.salazar.cheers.core.ui.ui.ErrorMessage

@ScreenPreviews
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        uiState = SignInUiState(isLoading = false),
        onSignInClick = { _ -> },
        navigateToSignUp = { /*TODO*/ },
        onUsernameChanged = { _ -> },

    )
}

@Composable
fun SignInScreen(
    uiState: SignInUiState,
    onSignInClick: (username: String) -> Unit,
    navigateToSignUp: () -> Unit,
    onUsernameChanged: (String) -> Unit,
    onGoogleClick: () -> Unit = {},
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
                UsernameTextField(uiState, onUsernameChanged = onUsernameChanged)
                Spacer(Modifier.height(8.dp))
                LoginButton(
                    isLoading = uiState.isLoading,
                    signInWithEmailPassword = {
                        onSignInClick(uiState.username)
                    },
                )
                ErrorMessage(
                    errorMessage = uiState.errorMessage,
                    paddingValues = PaddingValues(vertical = 8.dp)
                )
                GoogleButton(
                    onClicked = {},
                )
                TextDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    dayString = "OR",
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = navigateToSignUp,
                ) {
                    Text(text = "Register")
                }
            }
            Footer(
                navigateToSignUp = navigateToSignUp,
            )
        }
    }
}

@Composable
fun UsernameTextField(
    uiState: SignInUiState,
    onUsernameChanged: (String) -> Unit,
) {
    val username = uiState.username
    val focusManager = LocalFocusManager.current
    TextField(
        value = username,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        onValueChange = { onUsernameChanged(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        placeholder = { Text("Username") },
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
        text = "Sign in with passkey",
        isLoading = isLoading,
        onClick = signInWithEmailPassword,
        shape = MaterialTheme.shapes.medium,
        icon = Icons.Filled.Fingerprint,
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
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            val image = when(isSystemInDarkTheme()) {
                true -> com.salazar.cheers.core.ui.R.drawable.fido_alliance_white
                false -> com.salazar.cheers.core.ui.R.drawable.fido_alliance_black
            }
            Image(
                painter = painterResource(id = image),
                contentDescription = "fido alliance",
            )
        }
    }
}

@Composable
fun TextDivider(
    modifier: Modifier = Modifier,
    dayString: String,
) {
    Row(
        modifier = modifier
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
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        thickness = 2.dp,
    )
}