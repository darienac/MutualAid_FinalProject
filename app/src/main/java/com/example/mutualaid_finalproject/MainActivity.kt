package com.example.mutualaid_finalproject

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.mutualaid_finalproject.ui.ProfileScreen
import com.example.mutualaid_finalproject.ui.SearchScreen
import com.example.mutualaid_finalproject.ui.SettingsScreen
import com.example.mutualaid_finalproject.ui.SignInScreen
import com.example.mutualaid_finalproject.ui.theme.MutualAid_FinalProjectTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val credentialManager = CredentialManager.create(this)
    val coroutineScope = lifecycleScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MutualAid_FinalProjectTheme {
                MainNavigation(onLaunchSignIn = {launchSignIn()})
            }
        }
    }

    fun launchSignIn() {
        val getPasswordOption = GetPasswordOption()

        // Get passkey from the user's public key credential provider.
//        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
//            requestJson = "requestJson"
//        )

//        val getCredRequest = GetCredentialRequest(
//            listOf(getPasswordOption, getPublicKeyCredentialOption)
//        )

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // probably set this to true for signing in, false if registering
            .setServerClientId("38412906080-ki73o7cfjddsq7b0jkbd638dcmreqt4d.apps.googleusercontent.com")
            .setAutoSelectEnabled(false)
//            .setNonce(<nonce string to use when generating a Google ID token>)
        .build()

        val getCredRequest: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    // Use an activity-based context to avoid undefined system UI
                    // launching behavior.
                    context = this@MainActivity,
                    request = getCredRequest
                )
                handleSignIn(result)
            } catch (e : GetCredentialException) {
                Log.e("CredentialManager", e.errorMessage.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        // You can use the members of googleIdTokenCredential directly for UX
                        // purposes, but don't use them to store or control access to user
                        // data. For that you first need to validate the token:
                        // pass googleIdTokenCredential.getIdToken() to the backend server.

//                        GoogleIdTokenVerifier verifier = ... // see validation instructions
//                        GoogleIdToken idToken = verifier.verify(idTokenString);

                        // To get a stable account identifier (e.g. for storing user data),
                        // use the subject ID:

//                        idToken.getPayload().getSubject()
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("CredentialManager", "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e("CredentialManager", "Unexpected type of credential (custom)")
                }
            }
            else -> {
                Log.e("CredentialManager", "Unexpected type of credential")
            }
        }
    }
}

@Composable
fun MainNavigation(onLaunchSignIn: () -> Unit) { // Outermost composable where probably all/most of the UI logic can go
    var selectedItem by remember {mutableIntStateOf(0)}
    var signedIn by remember {mutableStateOf(false)} // Temporary until this can be checked directly

    if (!signedIn) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Box(modifier=Modifier.padding(innerPadding)) {
                SignInScreen(onSignInRequest=onLaunchSignIn) // This might be removed later if we can just directly launch the firebase UI
            }
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar() {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = {selectedItem = 0},
                    icon = {Icon(Icons.Filled.AccountCircle, "Profile")},
                    label = {Text("Profile")}
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = {selectedItem = 1},
                    icon = {Icon(Icons.Filled.Search, "Search")},
                    label = {Text("Search")}
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = {selectedItem = 2},
                    icon = {Icon(Icons.Filled.Settings, "Settings")},
                    label = {Text("Settings")}
                )
            }
        }
    ) { innerPadding ->
        Box(modifier=Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> ProfileScreen()
                1 -> SearchScreen()
                2 -> SettingsScreen()
            }
        }
    }
}