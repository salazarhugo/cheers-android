package com.salazar.cheers

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.salazar.cheers.databinding.ContentSignInBinding
import com.salazar.cheers.ui.theme.CheersTheme
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import com.snapchat.kit.sdk.login.api.FirebaseCustomTokenResultError

import com.snapchat.kit.sdk.login.api.FirebaseCustomTokenResultCallback

import com.snapchat.kit.sdk.SnapLogin

import com.snapchat.kit.sdk.login.api.SnapLoginApi
import org.jetbrains.anko.toast
import com.snapchat.kit.sdk.core.controller.LoginStateController
import com.snapchat.kit.sdk.core.controller.LoginStateController.OnLoginStateChangedListener


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        setContent {
            CheersTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AndroidViewBinding(ContentSignInBinding::inflate)
                }
            }
        }
//        signInWithSnapchat()
//        checkGithubCallback()
    }

    private fun signInWithSnapchat() {
        SnapLogin.getAuthTokenManager(this).startTokenGrant()

        val mLoginStateChangedListener: OnLoginStateChangedListener =
            object : OnLoginStateChangedListener {
                override fun onLoginSucceeded() {
                    // Here you could update UI to show login success
                    Log.e("SNAPCHAT DEBUG", "FWF")
                }

                override fun onLoginFailed() {
                    Log.e("SNAPCHAT DEBUG", "FWF")
                    // Here you could update UI to show login failure
                }

                override fun onLogout() {
                    // Here you could update UI to reflect logged out state
                    Log.e("SNAPCHAT DEBUG", "FWF")
                }
            }

        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(mLoginStateChangedListener);
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

    @OptIn(ExperimentalMaterialApi::class)
    @ExperimentalMaterial3Api
    private fun signInAnonymously() {
        auth.signInAnonymously().addOnSuccessListener {
            startActivity(intentFor<MainActivity>().newTask().clearTask())
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
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