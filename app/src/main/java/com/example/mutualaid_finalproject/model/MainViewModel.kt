package com.example.mutualaid_finalproject.model

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

data class UserData(
    val uid: String = "",
    val email: String = "",
    val emailVerified: Boolean = false
)

class MainViewModel(application: Application) : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    var currentUser = MutableLiveData<UserData?>()
    var profileRepository = ProfileRepository() // this is public so the UI controller can access this directly
    var postRepository = PostRepository() // this is public so the UI controller can access this directly
    var distanceCalculator = DistanceCalculator() // return number miles

    init {
        if (auth.currentUser != null) {
            updateCurrentUser(auth.currentUser)
        }
    }

    private fun updateCurrentUser(user: FirebaseUser?) {
        if (user?.uid == null) {
            profileRepository = ProfileRepository()
            postRepository = PostRepository()
            currentUser.value = null
        } else {
            profileRepository = ProfileRepository(user.uid) {
                currentUser.value = UserData(
                    uid=user.uid,
                    email=user.email?:"",
                    emailVerified=user.isEmailVerified
                )
            }
            postRepository = PostRepository(user.uid)
        }
    }

    fun logout() {
        profileRepository = ProfileRepository()
        currentUser.value = null
        auth.signOut()
    }

    fun handleSignUp(email: String, password: String, mainActivity: Activity) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "createUserWithEmail:success")
                    updateCurrentUser(auth.currentUser)
                } else {
                    Log.w("FirebaseAuth", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        mainActivity,
                        "Authentication failed. " + (task.exception?.message ?: ""),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun handleSignIn(email: String, password: String, mainActivity: Activity) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(mainActivity) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "signInWithEmail:success")
                    updateCurrentUser(auth.currentUser)
                } else {
                    Log.w("FirebaseAuth", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        mainActivity,
                        "Authentication failed. " + (task.exception?.message ?: ""),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun handleSignIn(result: GetCredentialResponse, mainActivity: Activity) {
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

//                        GoogleIdTokenVerifier verifier =  // see validation instructions
//                        GoogleIdToken idToken = verifier.verify(idTokenString);

                        // To get a stable account identifier (e.g. for storing user data),
                        // use the subject ID:

//                        idToken.getPayload().getSubject()
                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener(mainActivity) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FirebaseAuth", "signInWithCredential:success")
                                updateCurrentUser(auth.currentUser)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FirebaseAuth", "signInWithCredential:failure", task.exception)
                                Toast.makeText(
                                    mainActivity,
                                    "Authentication failed. " + (task.exception?.message ?: ""),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("CredentialManager", "Received an invalid google id token response", e)
                        Toast.makeText(
                            mainActivity,
                            "Authentication failed. Received an invalid google id token response",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                } else {
                    Log.e("CredentialManager", "Unexpected type of credential (custom)")
                    Toast.makeText(
                        mainActivity,
                        "Authentication failed. Unexpected type of credential (custom)",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            else -> {
                Log.e("CredentialManager", "Unexpected type of credential")
                Toast.makeText(
                    mainActivity,
                    "Authentication failed. Unexpected type of credential",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}