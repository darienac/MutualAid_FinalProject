package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PostViewingScreen(
    modifier: Modifier = Modifier,
    post: PostSearchResult,
    isEditable: Boolean = false,
    onClose: () -> Unit,
    onEditToggle: (Boolean) -> Unit
) {
    var isEditing by remember { mutableStateOf(isEditable) }
    var selectedType by remember { mutableStateOf(post.type) }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = if (isEditing) "Edit Post" else "View Post",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Close Button
        TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
            Text("Close")
        }

        // Title Field
        OutlinedTextField(
            value = post.title,
            onValueChange = { /* No-op since it's a viewing screen */ },
            label = { Text("Title") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        // Type: Radio Buttons
        Text(text = "Type", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedType == PostType.REQUEST,
                onClick = {
                    if (isEditing) selectedType = PostType.REQUEST
                },
                enabled = isEditing
            )
            Text(
                text = "Request",
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            RadioButton(
                selected = selectedType == PostType.OFFER,
                onClick = {
                    if (isEditing) selectedType = PostType.OFFER
                },
                enabled = isEditing
            )
            Text(text = "Offer", style = MaterialTheme.typography.bodyMedium)
        }

        // Location Field
        OutlinedTextField(
            value = post.location,
            onValueChange = { /* No-op */ },
            label = { Text("Location") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Distance: ${post.distance}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Status and Distance Display
        Text(
            text = "Accepted: ${if (post.isAccepted) "Yes" else "No"}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Edit Toggle Button
        TextButton(
            onClick = {
                isEditing = !isEditing
                onEditToggle(isEditing)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Switch to View Mode" else "Switch to Edit Mode")
        }
    }
}