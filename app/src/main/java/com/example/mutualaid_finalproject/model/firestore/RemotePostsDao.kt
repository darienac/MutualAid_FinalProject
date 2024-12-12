package com.example.mutualaid_finalproject.model.firestore

import android.util.Log
import com.example.mutualaid_finalproject.model.Post
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RemotePostsDao(private val pid: String, private val uid: String) {
    private val db = Firebase.firestore
    private val collection = db.collection("posts")
    private val TAG = "RemotePostsDao"

    private var currentPostRef = collection.document(pid)
    private val currentPostFlow = MutableSharedFlow<DocumentSnapshot?>(
        replay=1,
        onBufferOverflow= BufferOverflow.DROP_OLDEST
    )
    private var currentPostListener: ListenerRegistration? = null

    private var currentUserPostsRef = collection.whereEqualTo("uid", uid)
    private val currentUserPostsFlow = MutableSharedFlow<QuerySnapshot?>(
        replay=1,
        onBufferOverflow=BufferOverflow.DROP_OLDEST
    )
    private var currentUserPostsListener: ListenerRegistration? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        setCurrentPost(pid)
        setCurrentUser(uid)
    }

    fun toPost(document: DocumentSnapshot?): Post? {
        if (document == null || !document.exists()) {
            return null
        }
        return document.toObject<Post>()
    }

    fun toPosts(documents: QuerySnapshot?): List<Post> {
        if (documents == null) {
            return emptyList()
        }
        return documents.documents.map {
            toPost(it) ?: Post()
        }
    }

    fun setCurrentPost(pid: String) {
        currentPostListener?.remove()
        currentPostRef = collection.document(pid)
        currentPostListener = currentPostRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

//            if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
            // means this snapshot is local, pending being sent to server
//            }

            if (snapshot != null && snapshot.exists()) {
                coroutineScope.launch(Dispatchers.IO) {
                    currentPostFlow.emit(snapshot)
                }
            } else {
                coroutineScope.launch(Dispatchers.IO) {
                    currentPostFlow.emit(null)
                }
            }
        }
    }

    fun getCurrentPostFlow(): SharedFlow<DocumentSnapshot?> {
        return currentPostFlow.asSharedFlow()
    }

    fun setCurrentUser(uid: String) {
        currentUserPostsListener?.remove()
        currentUserPostsRef = collection.whereEqualTo("uid", uid)
        currentUserPostsListener = currentUserPostsRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

//            if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
            // means this snapshot is local, pending being sent to server
//            }

            if (snapshot != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    currentUserPostsFlow.emit(snapshot)
                }
            } else {
                coroutineScope.launch(Dispatchers.IO) {
                    currentUserPostsFlow.emit(null)
                }
            }
        }
    }

    fun getCurrentUserPostsFlow(): SharedFlow<QuerySnapshot?> {
        return currentUserPostsFlow.asSharedFlow()
    }

    fun get(pid: String, onResult:(DocumentSnapshot?)->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed get()")}) {
        collection.document(pid).get()
            .addOnSuccessListener(onResult)
            .addOnFailureListener(onError)
    }

    // Distance filtering can happen after the initial results are returned to shrink the list further
    fun search(onResult:(QuerySnapshot?)->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed search()")}, limit : Long = 1000) {
        collection.orderBy("date_posted", Query.Direction.DESCENDING).limit(limit).get()
            .addOnSuccessListener(onResult)
            .addOnFailureListener(onError)
    }

    fun set(post: Post, onResult:()->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed set()")}) {
        collection.document(post.pid).set(post)
            .addOnSuccessListener { onResult() }
            .addOnFailureListener(onError)
    }

    fun delete(pid: String, onResult:()->Unit, onError:(Exception)->Unit={Log.w(TAG, "Failed delete()")}) {
        collection.document(pid).delete()
            .addOnSuccessListener { onResult() }
            .addOnFailureListener(onError)
    }

    fun close() {
        currentPostListener?.remove()
        currentUserPostsListener?.remove()
    }
}