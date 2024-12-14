package com.example.mutualaid_finalproject.ui

import android.app.Activity
import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.example.mutualaid_finalproject.model.Post
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.Context
import android.util.Log


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

const val LOCATION_PERMISSION_REQUEST_CODE = 1

@Composable
fun SearchScreen(modifier: Modifier = Modifier, postSearchResults: List<PostSearchResult>, onSearch: (String, Float, String ) -> Unit, onPostClicked: (String) -> Unit, setLocation: (String) -> Unit) { // custom post object that includes distance
    // String for searchQuery, Float for maxDistance, String for all/request/offer
//    var searchQuery by remember { mutableStateOf("") }
    var maxDistance by remember { mutableStateOf(50f) } // Maximum distance in miles
//    var postTypeFilter by remember { mutableStateOf<PostType?>(null) } // No filter by default
//    val userLocation = "Boston, MA" // Change later, to see user's location
    var manualLocation by remember { mutableStateOf("") }
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


            // Manual Location Input
            OutlinedTextField(
                value = manualLocation,
                onValueChange = { manualLocation = it },
                label = { Text("Enter Location") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Button to Set Manual Location
            TextButton(onClick = {
                if (manualLocation.isNotBlank()) {
                    setLocation(manualLocation)
                }
            }) {
                Text("Set Manual Location")
            }

            val context = LocalContext.current
            var permissionGranted by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }

            if (!permissionGranted) {
                // Request permission
                TextButton(onClick = {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }) {
                    Text("Request Permission")
                }
            } else {
                // Permission granted, fetch location
                TextButton(onClick = {
                    getCurrentLocation(context) { location ->
                        setLocation(location)
                    }
                }) {
                    Text("Use Current Location")
                }
            }



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

fun getCurrentLocation(context: Context, onLocationReceived: (String) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = "${location.latitude}, ${location.longitude}"
                Log.d("LocationUpdate", "New location: Latitude = ${location.latitude}, Longitude = ${location.longitude}")
                onLocationReceived(currentLocation)
            } else {
                onLocationReceived("Location unavailable")
            }
        }
    }
}
