package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.widget.EdgeEffectCompat.getDistance

import com.example.mutualaid_finalproject.model.DistanceCalculator

data class Post(
    val postId: String, // UUID
    val type: PostType, // Enum for REQUEST or OFFER
    val isAccepted: Boolean,
    val title: String,
    val location: String
)

enum class PostType {
    REQUEST,
    OFFER
}
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

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    // List of sample posts
    val posts = listOf(
        Post(
            postId = "123",
            type = PostType.REQUEST,
            isAccepted = false,
            title = "Math Tutor",
            location = "Boston, MA"
        ),
        Post(
            postId = "124",
            type = PostType.OFFER,
            isAccepted = true,
            title = "Grocery Delivery",
            location = "Philadelphia, PA"
        ),
        Post(
            postId = "125",
            type = PostType.REQUEST,
            isAccepted = false,
            title = "Dog Walker Needed",
            location = "New York, NY"
        )
    )

    // State to hold the search query
    var searchQuery by remember { mutableStateOf("") }
    val userLocation = "Boston, MA"

    // Filter posts based on search query (case-insensitive)
    val filteredPosts = posts.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Search bar
        LocationInput(onSearch = { query ->
            searchQuery = query // Update the search query
        })

        // Display filtered posts
        if (filteredPosts.isEmpty()) {
            Text(
                text = "No results found",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            filteredPosts.forEach { post ->
                PostItem(post = post, userLocation = userLocation)
            }
        }
    }
}


@Composable
fun PostItem(post: Post, userLocation: String) {
    val distanceCalculator = DistanceCalculator()
    var distance by remember { mutableStateOf<String?>(null) }

    // Calculate the distance when the PostItem is composed
    LaunchedEffect(post.location, userLocation) {
        distance = distanceCalculator.getDistance(
            origin = userLocation,
            destination = post.location
        )
    }

    // Display post details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            distance?.let {
                Text(
                    text = "Distance: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            } ?: Text(
                text = "Calculating distance...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}