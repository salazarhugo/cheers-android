package com.salazar.cheers.ui.signin

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.MainActivity
import com.salazar.cheers.service.MyFirebaseMessagingService
import com.salazar.cheers.util.FirestoreUtil
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor

@ExperimentalMaterialApi
class CreatePasswordFragment : Fragment() {

    private val viewModel: CreateEmailPasswordViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val username = "username"
        auth = Firebase.auth

        return ComposeView(requireContext()).apply {
            setContent {
//                Text(username)
                CreatePasswordScreen()
            }
        }
    }

    @Composable
    fun CreatePasswordScreen() {
        val uiState = viewModel.uiState.collectAsState(CreateEmailPasswordViewModelState()).value

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text("Enter Phone or Email", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("You can't change it later")
            Spacer(Modifier.height(16.dp))
            EmailTextField(uiState)
            Spacer(Modifier.height(8.dp))
            PasswordTextField(uiState)
            Spacer(Modifier.height(8.dp))
            NextButton(uiState)
        }
    }

    @Composable
    fun NextButton(uiState: CreateEmailPasswordViewModelState) {
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = {
                createAccount(uiState.email, uiState.password)
            },
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
    fun PasswordTextField(uiState: CreateEmailPasswordViewModelState) {
        var passwordVisibility by remember { mutableStateOf(false) }
        TextField(
            value = uiState.password,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            onValueChange = {
                viewModel.onPasswordChanged(it)
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
    fun EmailTextField(uiState: CreateEmailPasswordViewModelState) {
        TextField(
            value = uiState.email,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            onValueChange = {
                viewModel.onEmailChanged(it)
            },
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
                if (uiState.isAvailable == true)
                    Icon(imageVector = Icons.Default.Check, "")
                else if (uiState.email.isNotEmpty())
                    IconButton(onClick = { viewModel.clearEmail() }) {
                        Icon(imageVector = Icons.Default.Close, "")
                    }
            },
            isError = uiState.isAvailable == false,
        )
        if (uiState.errorMessage.isNotEmpty())
            Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
//            reload();
        }
    }

    private fun createAccount(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")

                    signInSuccessful(email)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signInSuccessful(email: String) {
        FirestoreUtil.initCurrentUserIfFirstTime(email = email, username = "username") { user ->
            startActivity(intentFor<MainActivity>("user" to user).newTask().clearTask())
            getAndSaveRegistrationToken()
        }
    }

    private fun getAndSaveRegistrationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            MyFirebaseMessagingService.addTokenToFirestore(token)
        }
    }
//    private fun sendEmailVerification(email: String) {
//        Firebase.auth.sendSignInLinkToEmail(email, buildActionCodeSettings())
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d("CreateEmailPasswordAccount.kt", "Email sent.")
//                }
//            }
//    }
//
//    private fun buildActionCodeSettings(): ActionCodeSettings {
//        return actionCodeSettings {
//            url = "https://www.example.com/finishSignUp?cartId=1234"
//            handleCodeInApp = true
//            setAndroidPackageName(
//                "com.salazar.cheers",
//                true,
//                "28"
//            )
//        }
//    }
//
}
