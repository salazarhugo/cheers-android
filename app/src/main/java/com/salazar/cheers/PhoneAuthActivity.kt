package com.salazar.cheers

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.components.CircularProgressIndicatorM3
import com.salazar.cheers.components.DividerM3
import com.salazar.cheers.ui.signin.SignInViewModel
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PhoneAuthActivity : AppCompatActivity() {

    private val signInViewModel: SignInViewModel by viewModels()
    private val viewModel: PhoneAuthViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            CheersTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.systemBarsPadding(),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp
                ) {
                    PhoneAuthScreen()
                }
            }
        }
    }

    @Composable
    fun PhoneAuthScreen() {
        val uiState = viewModel.uiState.collectAsState().value
        initCallback(uiState)
        Tabs(uiState)
    }

    @Composable
    fun PhoneNumberScreen(uiState: PhoneAuthViewModelState) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PhoneNumberInput(uiState = uiState)
            Spacer(Modifier.height(16.dp))
            SendVerificationCodeButton(uiState = uiState)
        }
    }

    @Composable
    fun VerificationCodeScreen(uiState: PhoneAuthViewModelState) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VerificationCodeInput(uiState = uiState)
        }
    }

    @Composable
    fun Tabs(uiState: PhoneAuthViewModelState) {
        val tabs = 2
        val pagerState = uiState.pagerState

        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                count = tabs,
                state = pagerState,
                modifier = Modifier.height(300.dp)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(22.dp),
                ) {
                    when (page) {
                        0 -> PhoneNumberScreen(uiState = uiState)
                        1 -> VerificationCodeScreen(uiState = uiState)
                    }
                }
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
            )
            VerifyButton(uiState = uiState)
        }
    }

    @Composable
    fun ChooseUsernameScreen(uiState: PhoneAuthViewModelState) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text("Choose username", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("You can't change it later")
            Spacer(Modifier.height(36.dp))
//            UsernameTextField(uiState)
            Spacer(Modifier.height(8.dp))
        }
    }

    @Composable
    fun PhoneNumberInput(uiState: PhoneAuthViewModelState) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onPhoneNumberChange(it) },
            label = { Text("Phone number") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            enabled = !uiState.isLoading
        )
    }

    @Composable
    fun VerificationCodeInput(uiState: PhoneAuthViewModelState) {
        val verificationCode = uiState.verificationCode

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Verification code") },
            singleLine = true,
            value = verificationCode,
            onValueChange = { viewModel.onVerificationCodeChange(it) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
        )
    }

    @Composable
    fun VerifyButton(uiState: PhoneAuthViewModelState) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            DividerM3()
            Button(
                onClick = {
                    verifyPhoneNumberWithCode(uiState.verificationId, uiState.verificationCode)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = uiState.verificationCode.isNotBlank()
            ) {
                Text("Verify")
            }
        }
    }

    @Composable
    fun SendVerificationCodeButton(uiState: PhoneAuthViewModelState) {
        Column {
            Button(
                onClick = {
                    startPhoneNumberVerification(phoneNumber = uiState.phoneNumber)
                    viewModel.isLoadingChange(true)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                if (uiState.isLoading)
                    CircularProgressIndicatorM3()
                else
                    Text("Send verification code")
            }
        }
    }

    private fun initCallback(uiState: PhoneAuthViewModelState) {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                } else if (e is FirebaseTooManyRequestsException) {
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                viewModel.onCodeSent(verificationId = verificationId, token)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(
        verificationId: String?,
        code: String
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result?.user
                    signInSuccessful()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun signInSuccessful(acct: GoogleSignInAccount? = null) {
        FirestoreUtil.checkIfUserExists { exists ->
            if (exists) {
                startActivity(intentFor<MainActivity>().newTask().clearTask())
                signInViewModel.getAndSaveRegistrationToken()
            } else {
                FirestoreUtil.initCurrentUserIfFirstTime(
                    acct = acct,
                    username = UUID.randomUUID().toString()
                ) {
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                    signInViewModel.getAndSaveRegistrationToken()
                }
            }
        }
    }

    private fun updateUI(user: FirebaseUser? = auth.currentUser) {

    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }
}