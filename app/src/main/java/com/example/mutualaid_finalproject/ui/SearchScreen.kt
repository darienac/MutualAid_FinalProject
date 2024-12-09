package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
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
import com.example.mutualaid_finalproject.model.Post

//
//val samplePost1 = Post(
//    postId = "1",
//    type = PostType.REQUEST,
//    isAccepted = false,
//    title = "Need Help Studying for CS501!!!",
//    location = "665 Commonwealth Ave, Boston, MA 02215" //cds
//)
//
//val samplePost2 = Post(
//    postId = "1",
//    type = PostType.REQUEST,
//    isAccepted = false,
//    title = "Help Carry Groceries",
//    location = "1317 Beacon St, Brookline, MA 02446"    // trader joes
//)
//
//val samplePost3 = Post(
//    postId = "1",
//    type = PostType.REQUEST,
//    isAccepted = false,
//    title = "New York Mission Poggies!!!",
//    location = "New York City, NY"
//)

data class PostSearchResult(
    val postId: String, // UUID
    val type: PostType, // Enum for REQUEST or OFFER
    val isAccepted: Boolean,
    val title: String,
    val location: String,
    var distance: String = "Unknown" // Initialize with "Unknown" by default
)

enum class PostType {
    REQUEST,
    OFFER
}

data class Distance(val value: Float, val unit: String)

@Composable
fun SearchScreen(modifier: Modifier = Modifier, postSearchResults: List<PostSearchResult>, onSearch: (String, Float, String ) -> Unit, onPostClicked: (String) -> Unit) { // custom post object that includes distance
    // String for searchQuery, Float for maxDistance, String for all/request/offer
//    var searchQuery by remember { mutableStateOf("") }
    var maxDistance by remember { mutableStateOf(50f) } // Maximum distance in miles
//    var postTypeFilter by remember { mutableStateOf<PostType?>(null) } // No filter by default
    val userLocation = "Boston, MA" // Change later, to see user's location

    val filteredPosts = postSearchResults   // change to mutable list?
    // Post Type Filter with Radio Buttons
    val postTypeOptions = listOf("All", "Request", "Offer")
    var selectedOption by remember { mutableStateOf("All") }

//    // Filter posts based on search query, distance, and post type
//    val filteredPosts = posts.filter { post ->
//        // Check if post title matches the search query (case-insensitive)
//        val matchesTitle = post.title.contains(searchQuery, ignoreCase = true)
//
//        // Check if the distance matches the filter
//        val matchesDistance = post.distance.split(" ")[0].toFloatOrNull()?.let {
//            it <= maxDistance
//        } ?: false
//
//        // Check if the post type matches the selected filter (if any)
//        val matchesType = postTypeFilter?.let { post.type == it } ?: true
//        // All conditions must be true to include the post
//        matchesTitle && matchesDistance && matchesType
//    }

    // State to track the selected post
    var selectedPost by remember { mutableStateOf<PostSearchResult?>(null) }

    if (selectedPost != null) {
        // Render PostViewingScreen when a post is selected
        PostViewingScreen(
            post = selectedPost!!,
            isEditable = false,
            onClose = { selectedPost = null },
            onEditToggle = { /* Handle edit toggle logic here */ }
        )
    } else {

        Column(modifier = Modifier.padding(16.dp)) {
            // Search bar
            LocationInput(maxDistance, selectedOption, onSearch = onSearch)

            // Distance Slider (miles)
            Text(
                "Max Distance: ${maxDistance.toInt()} miles",
                modifier = Modifier.padding(top = 16.dp)
            )
            Slider(
                value = maxDistance,
                onValueChange = { maxDistance = it },
                valueRange = 0f..100f,
                steps = 10,
                modifier = Modifier.fillMaxWidth()
            )



            Row(modifier = Modifier.padding(top = 16.dp)) {
                Text("Post Type: ", modifier = Modifier.align(Alignment.CenterVertically))
                postTypeOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                        Text(option, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            LazyColumn {
                // Display filtered posts or a message if no results
                if (filteredPosts.isEmpty()) {
                    item {
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    filteredPosts.forEach { post ->
                        if (
                            (post.distance.endsWith(" mi") && post.distance.substring(
                                0,
                                post.distance.length - 3
                            ).toFloat() > maxDistance) ||
                            (selectedOption == "Request" && post.type != PostType.REQUEST) ||
                            (selectedOption == "Offer" && post.type != PostType.OFFER)
                        ) {
                            // Don't add to results
                        } else {
                            item(key = post.postId) {
                                PostItem(postSearchResult = post,
                                    onPostClicked = { postId ->
                                    selectedPost = postSearchResults.find { it.postId == postId } })
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun PostItem(postSearchResult: PostSearchResult, onPostClicked: (String) -> Unit) {
    // Display post details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPostClicked(postSearchResult.postId) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = postSearchResult.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Distance: ${postSearchResult.distance}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
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