package com.example.mutualaid_finalproject.ui

import NewPostScreen
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mutualaid_finalproject.model.Post

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
        imageUri: Uri?,
        location: String?,
        datePosted: String,
        dateLatest: String,
        tags: String
    ) -> Unit
) {
    var newPostScreenOpen by rememberSaveable{mutableStateOf(false)}
    var postSelected by rememberSaveable{mutableStateOf<String?>(null)}
    if (newPostScreenOpen) {
        InnerScreen(title="New Post", onClose={newPostScreenOpen=false}) {
            NewPostScreen(modifier, uid) {type,username,title,description,imageUri,location,datePosted,dateLatest,tags->
                newPostScreenOpen=false
                onNewPost(type, username, title, description, imageUri, location ,datePosted, dateLatest, tags)
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
        LazyColumn(modifier=Modifier.padding(innerPadding).padding(16.dp)) {
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
                PostItem(
                    PostSearchResult(
                        postId=posts[index].pid,
                        type=if (posts[index].type == "request") PostType.REQUEST else PostType.OFFER,
                        isAccepted=posts[index].accepted,
                        title=posts[index].title,
                        location=posts[index].location,
                        distance=""
                    )
                ) {

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyPostsPreview() {
    Box(modifier=Modifier.fillMaxSize()) {
        MyPostsScreen(modifier=Modifier, "", emptyList(), {_,_,_,_,_,_,_,_,_->})
    }
}