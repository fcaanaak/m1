package com.example.cpen321application

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.SecureRandom
import java.util.Base64
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


const val TAG = "LoginActivity"
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val webClientId = BuildConfig.GOOGLE_CLIENT_ID

        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> // Deal with this paddings
                    LoginButton(webClientId)// Update above constant before running
                }
            }
        }
    }
}

//This function is used to generate a secure nonce to pass in with our request
fun generateSecureRandomNonce(byteLength: Int = 32): String {
    val randomBytes = ByteArray(byteLength)
    SecureRandom.getInstanceStrong().nextBytes(randomBytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
}

@Composable
fun LoginButton(webClientId: String) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val onClick: () -> Unit = {
        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption
            .Builder(serverClientId = webClientId)
            .setNonce(generateSecureRandomNonce())
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        coroutineScope.launch {
            signIn(request, context)
        }
    }

    Image(
        painter = painterResource(id = R.drawable.signin_img),
        contentDescription = "",
        modifier = Modifier.fillMaxSize().clickable(enabled=true,onClick=onClick)
    )

}


// Modify this to fit how the actual app needs to work
suspend fun signIn(request: GetCredentialRequest, context: Context): Exception? {
    val credentialManager = CredentialManager.create(context)
    val failureMessage = "Sign in failed!"
    //using delay() here helps prevent NoCredentialException when the BottomSheet Flow is triggered
    //on the initial running of our app
    delay(250.milliseconds)
    return try {
        // The getCredential is called to request a credential from Credential Manager.
        val result = credentialManager.getCredential(
            request = request,
            context = context,
        )
        Log.i(TAG, result.toString())

        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Log.i(TAG, "Signed in as: ${googleIdTokenCredential.id}")
        }

        Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "(☞ﾟヮﾟ)☞  Sign in Successful!  ☜(ﾟヮﾟ☜)")
        null
    } catch (e: GoogleIdTokenParsingException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": Issue with parsing received GoogleIdToken", e)
        e
    } catch (e: NoCredentialException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": No credentials found", e)
        e
    } catch (e: GetCredentialCancellationException) {
        Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
        Log.e(TAG, "$failureMessage: Sign-in was cancelled", e)
        e
    } catch (e: GetCredentialCustomException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": Issue with custom credential request", e)
        e
    } catch (e: GetCredentialException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": Failure getting credentials", e)
        e
    }
}
