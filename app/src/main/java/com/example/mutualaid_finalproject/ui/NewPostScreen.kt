package com.example.mutualaid_finalproject.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun NewPostScreen(
    modifier: Modifier = Modifier,
    username: String,
    postFunction: (
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
    var type by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var datePosted by remember { mutableStateOf("") }
    var dateLatest by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Create New Post",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Type (request or offer)") },
            placeholder = { Text("Enter 'request' or 'offer'") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Write your post details...") },
            modifier = Modifier.fillMaxWidth()
        )

        // Image Upload Section
        Text(text = "Upload Image", style = MaterialTheme.typography.titleMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Select Image")
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            placeholder = { Text("Optional: Enter location details") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = datePosted,
            onValueChange = { datePosted = it },
            label = { Text("Date Posted") },
            placeholder = { Text("e.g., 2024-11-18") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dateLatest,
            onValueChange = { dateLatest = it },
            label = { Text("Date Latest") },
            placeholder = { Text("e.g., 2024-11-18") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags (comma-separated)") },
            placeholder = { Text("e.g., help, tutoring, food") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                postFunction(
                    type,
                    username,
                    title,
                    description,
                    imageUri,
                    if (location.isNotBlank()) location else null,
                    datePosted,
                    dateLatest,
                    tags
                )
                type = ""
                description = ""
                location = ""
                datePosted = ""
                dateLatest = ""
                tags = ""
                imageUri = null
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && type.isNotBlank() && description.isNotBlank()
        ) {
            Text(text = "Create Post")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewPostScreenPreview() {
    NewPostScreen(
        username="coolguy",
        postFunction = { type, username, title, description, imageUri, location, datePosted, dateLatest, tags ->
            println("Post created with:\nType: $type\nUsername: $username\nTitle: $title\nDescription: $description\nImage URI: $imageUri\nLocation: $location\nDate Posted: $datePosted\nDate Latest: $dateLatest\nTags: $tags")
        }
    )
}