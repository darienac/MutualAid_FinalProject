package com.example.mutualaid_finalproject.ui

import NewPostScreen
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults.filledIconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.mutualaid_finalproject.model.Post
import com.example.mutualaid_finalproject.ui.theme.MutualAid_FinalProjectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    modifier: Modifier = Modifier,
    uid: String,
    posts: List<Post>,
    onNewPost: (
        type: String,
        username: String,
        title: String,
        description: String,
        location: String?,
        datePosted: String,
        dateLatest: String,
        tags: String
    ) -> Unit,
    onPostRemoved: (String)->Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val useOnePane = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    var newPostScreenOpen by rememberSaveable{mutableStateOf(false)}
    var postSelected by rememberSaveable{mutableStateOf<String?>(null)}
    var removePostConfirmation by rememberSaveable{mutableStateOf<String?>(null)}

    if (removePostConfirmation != null) {
        BasicConfirmationDialog(title="Remove Post", desc="Delete this post? (Cannot be undone)", onConfirm={
            if (removePostConfirmation != null) {
                onPostRemoved(removePostConfirmation ?: "")
            }
            removePostConfirmation = null
        }, onCancel={
            removePostConfirmation = null
        })
    }

    if (newPostScreenOpen && useOnePane) {
        InnerScreen(title="New Post", onClose={newPostScreenOpen=false}) {
            NewPostScreen(modifier, uid) {type,username,title,description,location,datePosted,dateLatest,tags->
                newPostScreenOpen=false
                onNewPost(type, username, title, description, location ,datePosted, dateLatest, tags)
            }
        }
        return
    }

    Scaffold(
        contentWindowInsets=WindowInsets(0.dp),
        floatingActionButton={
            FloatingActionButton(onClick={newPostScreenOpen=true}) {
                Icon(Icons.Filled.Add, "New Post")
            }
        },
        modifier=Modifier.fillMaxSize()
    ) {innerPadding->
        if (useOnePane) {
            MyPostsScreen_PostsPane(modifier=Modifier.padding(innerPadding), posts=posts, onPostClicked={
                postSelected = it
                newPostScreenOpen = false
            }, onPostRemoved={
                postSelected = null
                removePostConfirmation = it
            })
        } else {
            Row(modifier = Modifier.padding(innerPadding)) {
                Box(modifier = Modifier.weight(1f)) {
                    MyPostsScreen_PostsPane(posts = posts, onPostClicked = {
                        postSelected = it
                        newPostScreenOpen = false
                    }, onPostRemoved = {
                        postSelected = null
                        removePostConfirmation = it
                    })
                }
                Box(modifier=Modifier.weight(1f)) {
                    if (newPostScreenOpen) {
                        NewPostScreen(modifier, uid) {type,username,title,description,location,datePosted,dateLatest,tags->
                            newPostScreenOpen=false
                            onNewPost(type, username, title, description, location ,datePosted, dateLatest, tags)
                        }
                    } else {
                        Text("No post selected", modifier=Modifier.padding(16.dp).fillMaxWidth(), textAlign=TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun MyPostsScreen_PostsPane(modifier: Modifier = Modifier, posts: List<Post>, onPostClicked: (String)->Unit, onPostRemoved: (String)->Unit) {
    LazyColumn(modifier=modifier.padding(16.dp)) {
        item {
            Text(
                text="My Posts",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier=Modifier.height(16.dp))
        }
        items(
            count=posts.size
        ) { index->
            MyPostsScreen_PostItemWithRemove(posts[index], onPostClicked, onPostRemoved)
        }
    }
}

@Composable
fun MyPostsScreen_PostItemWithRemove(post: Post, onPostClicked: (String)->Unit, onPostRemoved: (String)->Unit) {
    Row(verticalAlignment=Alignment.CenterVertically) {
        Box(modifier=Modifier.weight(1f)) {
            PostItem(
                PostSearchResult(
                    postId=post.pid,
                    type=if (post.type == "request") PostType.REQUEST else PostType.OFFER,
                    isAccepted=post.accepted,
                    title=post.title,
                    location=post.location,
                    distance=""
                ),
                onPostClicked=onPostClicked
            )
        }
        FilledIconButton(onClick={onPostRemoved(post.pid)}, colors=filledIconButtonColors(
            containerColor=MaterialTheme.colorScheme.errorContainer,
            contentColor=MaterialTheme.colorScheme.onErrorContainer
        )) {
            Icon(Icons.Filled.Clear, "Remove Post")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPostsPreview() {
    Box(modifier=Modifier.fillMaxSize()) {
        MyPostsScreen(modifier=Modifier, "", emptyList(), {_,_,_,_,_,_,_,_->}, {})
    }
}