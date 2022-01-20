package com.salazar.cheers

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.salazar.cheers.databinding.ContentSignInBinding
import com.salazar.cheers.service.MyFirebaseMessagingService
import com.salazar.cheers.ui.theme.CheersTheme
import com.snapchat.kit.sdk.SnapLogin
import com.snapchat.kit.sdk.core.controller.LoginStateController.OnLoginStateChangedListener
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            CheersTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
//                    Button(onClick = { signInWithSnapchat()}) {
//                       Text("SNAP")
//                    }
                    AndroidViewBinding(ContentSignInBinding::inflate)
                }
            }
        }
//        checkGithubCallback()
//        signInWithSnapchat()
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

        SnapLogin.getLoginStateController(this)
            .addOnLoginStateChangedListener(mLoginStateChangedListener)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser != null)
            signInSuccessful()
    }

    private fun checkGithubCallback() {
        val pendingResultTask: Task<AuthResult> = auth.pendingAuthResult ?: return
        pendingResultTask
            .addOnSuccessListener {
                signInSuccessful()
            }
            .addOnFailureListener {

            }
    }

    private fun signInWithGithub() {
        val provider = OAuthProvider.newBuilder("github.com")
        auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener {
                signInSuccessful()
            }
            .addOnFailureListener {

            }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously().addOnSuccessListener {
            startActivity(intentFor<MainActivity>().newTask().clearTask())
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    fun signInSuccessful(acct: GoogleSignInAccount? = null) {
//        FirestoreUtil.initCurrentUserIfFirstTime(acct) { user ->
//            startActivity(intentFor<MainActivity>().newTask().clearTask())
//            getAndSaveRegistrationToken()
//        }
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