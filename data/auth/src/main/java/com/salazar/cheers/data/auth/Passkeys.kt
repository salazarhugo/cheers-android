package com.salazar.cheers.data.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.salazar.cheers.shared.util.LocalActivity

@Composable
fun SignInWithPasskeys(
    onSuccess: (idToken: String) -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

        val getPasswordOption = GetPasswordOption()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(getPasswordOption)
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                context = activity.applicationContext,
                request = request,
            )
            handleSignIn(result, onSuccess)
        } catch (e : GetCredentialException) {
            e.printStackTrace()
//            handleFailure(e)
        }
    }
}

fun handleSignIn(
    result: GetCredentialResponse,
    onSuccess: (idToken: String) -> Unit,
) {
    val credential = result.credential

    when (credential) {
        is PublicKeyCredential -> {
            val responseJson = credential.authenticationResponseJson
        }
        is PasswordCredential -> {
            val username = credential.id
            val password = credential.password
        }
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract id to validate and
                    // authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    onSuccess(googleIdTokenCredential.idToken)
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("Passkeys", "Received an invalid google id token response", e)
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Log.e("Passkeys", "Unexpected type of credential")
            }
        }
        else -> {
        }
    }
}


fun handleFailure(
    e: CreateCredentialException,
) {
    when (e) {
        is CreatePublicKeyCredentialDomException -> {
            // Handle the passkey DOM errors thrown according to the
            // WebAuthn spec.
        }
        is CreateCredentialCancellationException -> {
            // The user intentionally canceled the operation and chose not
            // to register the credential.
        }
        is CreateCredentialInterruptedException -> {
            // Retry-able error. Consider retrying the call.
        }
        is CreateCredentialProviderConfigurationException -> {
            // Your app is missing the provider configuration dependency.
            // Most likely, you're missing the
            // "credentials-play-services-auth" module.
        }
        is CreateCredentialUnknownException -> {}
        is CreateCredentialCustomException -> {
            // You have encountered an error from a 3rd-party SDK. If you
            // make the API call with a request object that's a subclass of
            // CreateCustomCredentialRequest using a 3rd-party SDK, then you
            // should check for any custom exception type constants within
            // that SDK to match with e.type. Otherwise, drop or log the
            // exception.
        }
        else -> Log.w("Passkeys", "Unexpected exception type ${e::class.java.name}")
    }
}