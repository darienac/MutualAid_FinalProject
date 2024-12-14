package com.example.mutualaid_finalproject.model

import com.example.mutualaid_finalproject.model.firestore.RemotePostsDao
import com.example.mutualaid_finalproject.model.firestore.RemoteProfilesDao
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.map

class PostRepository(private val uid: String = "NO_USER") {
    private var remotePostsDao = RemotePostsDao("NO_POST", uid)

    var currentPost = remotePostsDao.getCurrentPostFlow().map { value: DocumentSnapshot? ->
        remotePostsDao.toPost(value)
    }
    var currentUserPosts = remotePostsDao.getCurrentUserPostsFlow().map { value: QuerySnapshot? ->
        remotePostsDao.toPosts(value)
    }

    fun setListeningPost(pid: String = "NO_POST") {
        remotePostsDao.setCurrentPost(pid)
    }

    fun get(pid: String, onResult: (Post?) -> Unit) {
        remotePostsDao.get(pid, onResult = { document ->
            val post = remotePostsDao.toPost(document)
            onResult(post)  // Ensure the result is properly passed.
        })
    }

    fun search(searchText: String, onResult: (MutableList<Post>) -> Unit) {
        remotePostsDao.search(onResult = { documents ->
            if (documents == null) {
                onResult(mutableListOf())
            } else {
                val posts = mutableListOf<Post>()
                for (document in documents) {
                    val post = remotePostsDao.toPost(document) ?: continue
                    if (post.title.contains(searchText, ignoreCase = true) ||
                        post.description.contains(searchText, ignoreCase = true)
                    ) {
                        posts.add(post)
                    }
                }
                onResult(posts)
            }
        })
    }

    fun set(post: Post, onResult: () -> Unit) {
        remotePostsDao.set(post, onResult)
    }

    fun delete(pid: String, onResult: () -> Unit) {
        remotePostsDao.delete(pid, onResult = onResult)
    }
}