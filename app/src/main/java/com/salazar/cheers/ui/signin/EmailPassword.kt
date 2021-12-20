package com.salazar.cheers.ui.signin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.salazar.cheers.MainActivity
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor

@ExperimentalMaterialApi
@AndroidEntryPoint
class EmailPasswordFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()

        return ComposeView(requireContext()).apply {
            setContent {
                EmailPasswordScreen()
            }
        }
    }

    @Composable
    fun EmailPasswordScreen() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            EmailTextField()
            Spacer(Modifier.height(8.dp))
            PasswordTextField()
            Spacer(Modifier.height(16.dp))
            LoginButton()
            Spacer(Modifier.height(8.dp))
            TextDivider(dayString = "OR")
            Spacer(Modifier.height(8.dp))
            Text(
                "Sign up",
                modifier = Modifier.clickable {
                    val action =
                        EmailPasswordFragmentDirections.actionEmailPasswordFragmentToChooseUsernameFragment()
                    findNavController().navigate(action)
                }
            )
        }
    }

    @Composable
    fun LoginButton() {
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = { signIn(viewModel.email.value, viewModel.password.value) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(text = "Log In")
        }
    }

    @Composable
    fun PasswordTextField() {
        val password = viewModel.password
        var passwordVisibility by remember { mutableStateOf(false) }
        TextField(
            value = password.value,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            onValueChange = {
                viewModel.onPasswordChange(it)
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
    fun EmailTextField() {
        val email = viewModel.email
        val focusManager = LocalFocusManager.current
        TextField(
            value = email.value,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            onValueChange = {
                viewModel.onEmailChange(it)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            placeholder = { Text("Email address") }
        )
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
        // TODO (M3): No Divider, replace when available
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    signInSuccessful(email)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun signInSuccessful(email: String) {
        FirestoreUtil.initCurrentUserIfFirstTime(email = email) { user ->
            startActivity(intentFor<MainActivity>("user" to user).newTask().clearTask())
        }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(requireActivity()) { task ->
                // Email Verification sent
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }
}
