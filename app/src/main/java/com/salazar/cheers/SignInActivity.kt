package com.salazar.cheers

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.salazar.cheers.components.EmailButton
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.workout.components.GoogleButton
import com.salazar.workout.components.TwitterButton
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast


class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    @ExperimentalMaterial3Api
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setContent {
            CheersTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_outline_face_24),
                            contentDescription = "App Logo",
                            Modifier.size(128.dp)
                        )
                        GoogleButton(onClicked = { signInWithGoogle() })
                        Spacer(modifier = Modifier.height(16.dp))
                        TwitterButton { signInWithTwitter()}
                        Spacer(modifier = Modifier.height(16.dp))
                        EmailButton {

                        }
                    }
                }
            }
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        checkGithubCallback()
    }

    private fun signInWithTwitter() {
        //TODO("Not yet implemented")
    }

    @ExperimentalMaterial3Api
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser != null)
            signInSuccessful()
    }

    @ExperimentalMaterial3Api
    private fun checkGithubCallback() {
        val pendingResultTask: Task<AuthResult> = auth.pendingAuthResult ?: return
        pendingResultTask
            .addOnSuccessListener {
                signInSuccessful()
            }
            .addOnFailureListener {

            }
    }

    private fun signInWithFacebook() {
//        TODO("FACEBOOK SIGN IN")
    }

    @ExperimentalMaterial3Api
    private fun signInWithGithub() {
        val provider = OAuthProvider.newBuilder("github.com")
        auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                signInSuccessful()
            }
            .addOnFailureListener {

            }
    }

    @ExperimentalMaterial3Api
    private fun signInAnonymously() {
        auth.signInAnonymously().addOnSuccessListener {
            startActivity(intentFor<MainActivity>().newTask().clearTask())
        }
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

                toast("YOO")
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    Log.w("GoogleSignIn", "Google sign in failed", e)
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    @ExperimentalMaterial3Api
    private fun signInSuccessful(acct: GoogleSignInAccount? = null) {
        FirestoreUtil.initCurrentUserIfFirstTime(acct) { user ->
            startActivity(intentFor<MainActivity>("user" to user).newTask().clearTask())
        }
    }

    @ExperimentalMaterial3Api
    private fun emailPasswordSignIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    signInSuccessful()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @ExperimentalMaterial3Api
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
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
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        const val FACEBOOK_TAG = "FACEBOOK"
    }
}