package com.example.mutualaid_finalproject.model.firestore

import android.util.Log
import com.example.mutualaid_finalproject.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class RemoteProfilesDao {
    private val db = Firebase.firestore
    private val collection = db.collection("profiles")
    private val TAG = "RemoteProfilesDao"

    fun toProfile(document: DocumentSnapshot?): Profile? {
        if (document == null || !document.exists()) {
            return null
        }
        return document.toObject<Profile>()
    }

    fun get(uid: String, onResult:(DocumentSnapshot?)->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed get()")}) {
        collection.document(uid).get()
            .addOnSuccessListener(onResult)
            .addOnFailureListener(onError)
    }

    fun set(profile: Profile, onResult:()->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed set()")}) {
        collection.document(profile.uid).set(profile)
            .addOnSuccessListener { onResult() }
            .addOnFailureListener(onError)
    }
}