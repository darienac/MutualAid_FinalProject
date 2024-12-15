package com.example.mutualaid_finalproject.model.firestore

import android.util.Log
import com.example.mutualaid_finalproject.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class RemoteProfilesDao(private val uid: String) {
    private val db = Firebase.firestore
    private val collection = db.collection("profiles")
    private val TAG = "RemoteProfilesDao"

    private val userProfileRef = collection.document(uid)
    private val userProfileFlow = MutableSharedFlow<DocumentSnapshot?>(
        replay=1,
        onBufferOverflow=BufferOverflow.DROP_OLDEST
    )
    private var listener: ListenerRegistration? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        listener = userProfileRef.addSnapshotListener {snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

//            if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                // means this snapshot is local, pending being sent to server
//            }

            if (snapshot != null && snapshot.exists()) {
                coroutineScope.launch(Dispatchers.IO) {
                    userProfileFlow.emit(snapshot)
                }
            } else {
                coroutineScope.launch(Dispatchers.IO) {
                    userProfileFlow.emit(null)
                }
            }
        }
    }

    fun toProfile(document: DocumentSnapshot?): Profile? {
        if (document == null || !document.exists()) {
            return null
        }
        return document.toObject<Profile>()
    }

    fun getCurrentUserFlow(): SharedFlow<DocumentSnapshot?> {
        return userProfileFlow.asSharedFlow()
    }

    fun get(uid: String, onResult:(DocumentSnapshot?)->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed get()")}) {
        collection.document(uid).get()
            .addOnSuccessListener(onResult)
            .addOnFailureListener(onError)
    }

    fun getList(uids: List<String>, onResult:(DocumentSnapshot?)->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed getList()")}) {
        for (uid in uids) {
            get(uid, onResult=onResult, onError=onError)
        }
    }

    fun set(profile: Profile, onResult:()->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed set()")}) {
        collection.document(profile.uid).set(profile)
            .addOnSuccessListener { onResult() }
            .addOnFailureListener(onError)
    }

    fun delete(uid: String, onResult:()->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed delete()")}) {
        collection.document(uid).delete()
            .addOnSuccessListener { onResult() }
            .addOnFailureListener(onError)
    }

    fun close() {
        listener?.remove()
    }
}