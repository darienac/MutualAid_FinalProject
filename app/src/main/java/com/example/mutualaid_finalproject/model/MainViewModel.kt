package com.example.mutualaid_finalproject.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainViewModel(application: Application) : ViewModel() {
    private val db = Firebase.firestore

    init {}

    fun createNewUser(username: String) {
        val profile = hashMapOf(
            "availability" to "",
            "name" to "test",
            "resources" to "textbooks",
            "skills" to "none"
        )

        db.collection("profiles").document(username)
            .set(profile)
            .addOnFailureListener {
                Log.w("MainViewModel", "Error adding document", it)
            }
    }
}