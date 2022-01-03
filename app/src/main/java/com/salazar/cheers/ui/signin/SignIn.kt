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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.salazar.cheers.MainActivity
import com.salazar.cheers.R
import com.salazar.cheers.components.FacebookButton
import com.salazar.cheers.components.GoogleButton
import com.salazar.cheers.components.TwitterButton
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreUtil
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()
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
                SignInScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SignInScreen() {

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
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Cheers", style = Typography.displayLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(100.dp))
                    GoogleButton {
                        signInWithGoogle()
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextDivider(dayString = "OR")
                    Spacer(modifier = Modifier.height(16.dp))
                    FacebookButton() {

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        onClick = {
                            val action =
                                SignInFragmentDirections.actionSignInFragmentToEmailPasswordFragment()
                            findNavController().navigate(action)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF32B2B),
                        )
                    ) {
                        Icon(Icons.Default.Email, "", tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("Sign in with Email", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF27F34E),
                        ),
                        shape = RoundedCornerShape(8.dp),
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
    fun Footer() {
        Column(
            modifier = Modifier.padding(13.dp)
        ) {
            Text(
                "By signing up, you agree to out Terms of Service and Privacy Policy",
                textAlign = TextAlign.Center,
                style = Typography.labelMedium
            )
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
        // TODO (M3): No Divider, replace when available
        Divider(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    @ExperimentalMaterial3Api
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
                    val user = auth.currentUser
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

    @OptIn(ExperimentalMaterial3Api::class)
    private fun signInSuccessful(acct: GoogleSignInAccount? = null) {
        FirestoreUtil.initCurrentUserIfFirstTime(acct) { user ->
            startActivity(intentFor<MainActivity>("user" to user).newTask().clearTask())
        }
    }
}
