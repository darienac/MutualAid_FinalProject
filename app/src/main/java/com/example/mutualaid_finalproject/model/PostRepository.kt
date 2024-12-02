package com.example.mutualaid_finalproject.model

import com.example.mutualaid_finalproject.model.firestore.RemotePostsDao
import com.example.mutualaid_finalproject.model.firestore.RemoteProfilesDao
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.map

class PostRepository {
    private var remotePostsDao = RemotePostsDao("NO_POST")

    var currentPost = remotePostsDao.getCurrentPostFlow().map {
            value: DocumentSnapshot? -> remotePostsDao.toPost(value)
    }

    fun setListeningPost(pid: String = "NO_POST") {
        remotePostsDao.close()
        remotePostsDao = RemotePostsDao(pid)
        currentPost = remotePostsDao.getCurrentPostFlow().map {
                value: DocumentSnapshot? -> remotePostsDao.toPost(value)
        }
    }

    fun get(pid: String, onResult:(Post?)->Unit) {
        remotePostsDao.get(pid, onResult={
            onResult(remotePostsDao.toPost(it))
        })
    }

    fun set(post: Post, onResult:()->Unit) {
        remotePostsDao.set(post, onResult=onResult)
    }
}