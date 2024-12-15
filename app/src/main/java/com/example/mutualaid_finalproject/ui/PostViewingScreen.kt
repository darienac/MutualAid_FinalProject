package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mutualaid_finalproject.model.Post
import com.example.mutualaid_finalproject.model.Profile
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PostViewingScreen(
    modifier: Modifier = Modifier,
    post: Post?,
    profile: Profile?,
    isEditable: Boolean = false,
    onClose: () -> Unit,
    onPostEdit: (Post) -> Unit
) {
    var viewingProfile by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(post?.title ?: "") }
    var description by remember { mutableStateOf(post?.description ?: "") }
    var location by remember { mutableStateOf(post?.location ?: "") }
    var expirationDate by remember { mutableStateOf(post?.date_expires?.toDate()) }
    var accepted by remember { mutableStateOf(post?.accepted ?: false) }
    var selectedType by remember { mutableStateOf(post?.type ?: "") }

    var lastViewedPost by remember { mutableStateOf<Post?>(null) }
    if (lastViewedPost != post) { // fixes a bug where state values don't change after selected post changes
        lastViewedPost = post
        viewingProfile = false
        title = post?.title ?: ""
        description = post?.description ?: ""
        location = post?.location ?: ""
        expirationDate = post?.date_expires?.toDate()
        accepted = post?.accepted ?: false
        selectedType = post?.type ?: ""
    }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    if (viewingProfile && profile != null) {
        InnerScreen(title="Post Profile", onClose={viewingProfile=false}) {
            ProfileScreen(
                allowEdits=false,
                email=profile.email,
                phoneNumber=profile.phoneNumber,
                name=profile.name,
                description=profile.description,
                skills=profile.skills,
                resources=profile.resources,
                availability=profile.daysAvailable
            )
        }
        return
    }


    Column(modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)) {

        Text(
            text = if (isEditable) "Edit Post" else "View Post",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center)
        if (post == null){
            Text("No post selected", modifier=Modifier.padding(16.dp).fillMaxWidth(), textAlign=TextAlign.Center)
        }
        else{
            // Close button

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Close",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clickable { onClose() },

                    )
            }
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier
                    .fillMaxSize()
//            .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Title



//        // Close Button
//        item {
//            Box(modifier = Modifier.fillMaxWidth()) {
//                TextButton(
//                    onClick = onClose,
//
//                ) {
//                    Text("Close")
//                }
//            }
//        }

                // UID clickable Card (only in viewing mode)
                item {
                    if (!isEditable) {
                        SectionHeader("Profile")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewingProfile = true
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = profile?.name ?: "No User Found",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Title Field
                item{if (isEditable) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    SectionHeader("Title")
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
                }}

                // Description Field
                item{if (isEditable) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    SectionHeader("Description")
                    Text(text = description, style = MaterialTheme.typography.bodyLarge)
                }}

                // Location Field
                item{if (isEditable) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    SectionHeader("Location")
                    Text(text = location, style = MaterialTheme.typography.bodyLarge)
                }}

                // Post Type

                item{
                    SectionHeader("Type")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedType == "request",
                            onClick = { if (isEditable) selectedType = "request" },
                            enabled = isEditable
                        )
                        Text(text = "Request", modifier = Modifier.padding(end = 16.dp))

                        RadioButton(
                            selected = selectedType == "offer",
                            onClick = { if (isEditable) selectedType = "offer" },
                            enabled = isEditable
                        )
                        Text(text = "Offer")
                    }}

                // Date Posted
                item {
                    SectionHeader("Date Posted")
                    Text(
                        text = dateFormatter.format(post.date_posted.toDate()),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // Expiration Date
                item {
                    SectionHeader("Expiration Date")
                    Text(
                        text = dateFormatter.format(expirationDate),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Accepted Field
                item{if (isEditable) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Accepted: ", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = accepted,
                            onCheckedChange = { accepted = it }
                        )
                    }
                } else {
                    SectionHeader("Accepted")
                    Text(
                        text = if (accepted) "Yes" else "No",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }}

                // Save Button
                item {
                    if (post != null) {
                        Row(horizontalArrangement=Arrangement.Center, modifier=Modifier.fillMaxWidth()) {
                            Button(onClick={
                                onPostEdit(Post(
                                    pid=post.pid,
                                    accepted=accepted,
                                    date_expires=post.date_expires,
                                    date_posted=post.date_posted,
                                    description=description,
                                    location=location,
                                    title=title,
                                    type=selectedType,
                                    uid=post.uid
                                ))
                            }) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}