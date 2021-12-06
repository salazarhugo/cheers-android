package com.salazar.cheers

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.salazar.cheers.components.GoogleButton
import com.salazar.cheers.components.TwitterButton
import com.salazar.cheers.databinding.ContentMainBinding
import com.salazar.cheers.databinding.ContentSignInBinding
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.ui.theme.Typography
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setContent {
            CheersTheme() {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AndroidViewBinding(ContentSignInBinding::inflate)
                }
            }
        }

//        checkGithubCallback()
    }

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
    fun signInSuccessful(acct: GoogleSignInAccount? = null) {
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
}