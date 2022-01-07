package com.salazar.cheers.ui.signin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.components.*
import com.salazar.cheers.service.MyFirebaseMessagingService
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreUtil
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()
    private val args: SignInFragmentArgs by navArgs()

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        auth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        return ComposeView(requireContext()).apply {
            setContent {
                if (args.username != null) {
                    val signInIntent = mGoogleSignInClient.signInIntent
                    startForResult.launch(signInIntent)
                }
                SignInScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SignInScreen() {

        val uiState = viewModel.uiState.collectAsState().value

        if (uiState.isSignedIn)
            signIn()

        val state = remember {
            MutableTransitionState(false).apply {
                targetState = true
            }
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
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Text("Cheers", style = Typography.displayLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(60.dp))
                    EmailTextField(uiState)
                    Spacer(Modifier.height(8.dp))
                    PasswordTextField(uiState)
                    Spacer(Modifier.height(16.dp))
                    LoginButton(uiState)
                    if (uiState.errorMessage != null)
                        Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextDivider(dayString = "OR")
                    Spacer(modifier = Modifier.height(16.dp))
                    GoogleButton { signInWithGoogle() }
                    Spacer(modifier = Modifier.height(16.dp))
                    FacebookButton { }
                    Spacer(modifier = Modifier.height(16.dp))
//                    Button(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp),
//                        onClick = {
//                            val action =
//                                SignInFragmentDirections.actionSignInFragmentToEmailPasswordFragment()
//                            findNavController().navigate(action)
//                        },
//                        shape = RoundedCornerShape(4.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFFF32B2B),
//                        )
//                    ) {
//                        Icon(Icons.Default.Email, "", tint = Color.White)
//                        Spacer(Modifier.width(12.dp))
//                        Text("Sign in with Email", color = Color.White)
//                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF27F34E),
                        ),
                        shape = RoundedCornerShape(4.dp),
                        enabled = false,
                    ) {
                        Icon(Icons.Default.Phone, "", tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("Sign in with Phone", color = Color.White)
                    }
                }
                Footer()
            }
        }
    }

    @Composable
    fun PasswordTextField(uiState: SignInUiState) {
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
    fun EmailTextField(uiState: SignInUiState) {
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
            placeholder = { Text("Email address") },
            enabled = !uiState.isLoading,
        )
    }

    @Composable
    fun LoginButton(uiState: SignInUiState) {
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = {
                viewModel.signInWithEmailPassword()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = !uiState.isLoading,
        ) {
            if (uiState.isLoading)
                CircularProgressIndicatorM3()
            else
                Text(text = "Log In")
        }
    }

    @Composable
    fun Footer() {
        Column(
            modifier = Modifier.clickable {
                val action =
                    SignInFragmentDirections.actionSignInFragmentToChooseUsernameFragment()
                findNavController().navigate(action)
            }
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

    @OptIn(ExperimentalMaterial3Api::class)
    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    Log.w("GoogleSignIn", "Google sign in failed", e)
                    Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    @ExperimentalMaterial3Api
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                task.addOnFailureListener {
                    Log.e("GOOGLE", it.toString())
                }
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    signInSuccessful(acct)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signIn() {
        startActivity(intentFor<MainActivity>().newTask().clearTask())
        getAndSaveRegistrationToken()
    }

    private fun signInSuccessful(acct: GoogleSignInAccount? = null) {
        FirestoreUtil.checkIfUserExists { exists ->
            if (exists) {
                startActivity(intentFor<MainActivity>().newTask().clearTask())
                getAndSaveRegistrationToken()
            } else {
                val username = args.username
                if (username != null)
                    FirestoreUtil.initCurrentUserIfFirstTime(acct = acct, username = username) {
                        startActivity(intentFor<MainActivity>().newTask().clearTask())
                        getAndSaveRegistrationToken()
                    }
                else {
                    val action =
                        SignInFragmentDirections.actionSignInFragmentToChooseUsernameFragment(
                            isFromGoogle = true
                        )
                    findNavController().navigate(action)
                }
            }
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
}
