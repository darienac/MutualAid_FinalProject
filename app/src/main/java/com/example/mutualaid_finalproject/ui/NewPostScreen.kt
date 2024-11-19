package com.example.mutualaid_finalproject.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NewPostScreen(
    modifier: Modifier = Modifier,
    postFunction: (String) -> Unit,
    logoutFunction: () -> Unit
) {
    var post by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = post,
            onValueChange = { post = it },
            label = { Text("Post") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                postFunction(post)
                post = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Post")
        }
        TextButton(
            onClick = logoutFunction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout")
        }
    }
}