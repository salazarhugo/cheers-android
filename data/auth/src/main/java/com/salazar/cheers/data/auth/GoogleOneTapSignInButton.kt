package com.salazar.cheers.data.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException

@Composable
fun GoogleOneTapSignInButton(onSignIn: (String) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            try {
                val credential =
                    Identity.getSignInClient(context).getSignInCredentialFromIntent(it.data)
                val idtoken = credential.googleIdToken
                val username = credential.id
                val password = credential.password
                when {
                    idtoken != null -> {
                        // got an id token from google. use it to authenticate
                        // with your backend.
                        Log.d("Auth", "got id token.")
                        onSignIn(idtoken)
                    }

                    password != null -> {
                        // got a saved username and password. use them to authenticate
                        // with your backend.
                        Log.d("Auth", "got password.")
                    }

                    else -> {
                        // Shouldn't happen.
                        Log.d("Auth", "No ID token or password!")
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        },
    )

    LaunchedEffect(Unit) {
        val request = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(false)
            .build()

        Identity.getSignInClient(context).beginSignIn(request)
            .addOnSuccessListener {
                val intentSenderRequest =
                    IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                try {
                    launcher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .addOnFailureListener { e ->
                Log.d("Google Identity", e.localizedMessage)
            }
    }
}
