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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.window.core.layout.WindowWidthSizeClass
import java.text.SimpleDateFormat
import java.util.Locale


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
    var selectedOption by remember { mutableStateOf("All")}

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val useOnePane = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT



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
    val dummyPost = Post(
        pid = "12345",
        accepted = false,
        date_expires = com.google.firebase.Timestamp.now(), // Current timestamp
        date_posted = com.google.firebase.Timestamp.now(), // Current timestamp
        description = "This is a test post description.",
        location = "This is a test location.",
        title = "Test Post Title",
        type = "Request",
        uid = "Mr. Meowers"
    )
    // phone + post selected
    if (useOnePane && selectedPost != null) {
        // Render PostViewingScreen when a post is selected
        PostViewingScreen(
            post = dummyPost,
            isEditable = false,
            onClose = { selectedPost = null },
            onEditToggle = { /* Handle edit toggle logic here */ }
        )
    // phone + no post selected
    } else if (useOnePane) {

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

            // Row for Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Space buttons evenly
            ) {

                // Button to Set Manual Location
                TextButton(onClick = {
                    if (manualLocation.isNotBlank()) {
                        setLocation(manualLocation)
                        manualLocation = ""
                    }
                },
                    modifier = Modifier
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        )) {
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

                // Observe permission changes
                LaunchedEffect(Unit) {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    permissionGranted = permissionCheck == PackageManager.PERMISSION_GRANTED
                }

                if (!permissionGranted) {
                    // Request permission
                    TextButton(
                        onClick = {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                LOCATION_PERMISSION_REQUEST_CODE
                            )
                        },
                        modifier = Modifier
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text("Request Permission")
                    }
                } else {
                    // Permission granted, fetch location
                    TextButton(
                        onClick = {
                            getCurrentLocation(context) { location ->
                                setLocation(location)
                            }
                        },
                        modifier = Modifier
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text("Use Current Location")
                    }
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
    else{
        // In tablet layout, show the search UI on one side and post details on the other
        Row(modifier = Modifier.fillMaxSize()) {
            // Search UI on the left
            Box(modifier = Modifier.weight(1f)) {
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

                    // Row for Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly // Space buttons evenly
                    ) {

                        // Button to Set Manual Location
                        TextButton(onClick = {
                            if (manualLocation.isNotBlank()) {
                                setLocation(manualLocation)
                                manualLocation = ""
                            }
                        },
                            modifier = Modifier
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                )) {
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

                        // Observe permission changes
                        LaunchedEffect(Unit) {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            permissionGranted = permissionCheck == PackageManager.PERMISSION_GRANTED
                        }

                        if (!permissionGranted) {
                            // Request permission
                            TextButton(
                                onClick = {
                                    ActivityCompat.requestPermissions(
                                        context as Activity,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                        LOCATION_PERMISSION_REQUEST_CODE
                                    )
                                },
                                modifier = Modifier
                                    .border(
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Text("Request Permission")
                            }
                        } else {
                            // Permission granted, fetch location
                            TextButton(
                                onClick = {
                                    getCurrentLocation(context) { location ->
                                        setLocation(location)
                                    }
                                },
                                modifier = Modifier
                                    .border(
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Text("Use Current Location")
                            }
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

            // Post details on the right
            Box(modifier = Modifier.weight(1f)) {

                if (selectedPost == null){
                    PostViewingScreen(
                        post = null,
                        isEditable = false,
                        onClose = { selectedPost = null },
                        onEditToggle = { /* Handle edit toggle logic here */ }
                )}
                else {
                    PostViewingScreen(
                        post = dummyPost,
                        isEditable = false,
                        onClose = { selectedPost = null },
                        onEditToggle = { /* Handle edit toggle logic here */ }
                    )
                }
            }
        }
    }
}



@Composable
fun PostViewingScreen(
    modifier: Modifier = Modifier,
    post: Post?,
    isEditable: Boolean = false,
    onClose: () -> Unit,
    onEditToggle: (Boolean) -> Unit
) {
    var isEditing by remember { mutableStateOf(isEditable) }
    var title by remember { mutableStateOf(post?.title ?: "") }
    var description by remember { mutableStateOf(post?.description ?: "") }
    var location by remember { mutableStateOf(post?.location ?: "") }
    var expirationDate by remember { mutableStateOf(post?.date_expires?.toDate()) }
    var accepted by remember { mutableStateOf(post?.accepted ?: false) }
    var selectedType by remember { mutableStateOf(post?.type ?: "") }


    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    Column(modifier = Modifier.padding(16.dp)) {

    Text(
        text = if (isEditing) "Edit Post" else "View Post",
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

        // Title Field

        item{if (isEditing) {
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
        item{if (isEditing) {
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
        item{if (isEditing) {
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
                selected = selectedType == "Request",
                onClick = { if (isEditing) selectedType = "Request" },
                enabled = isEditing
            )
            Text(text = "Request", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = selectedType == "Offer",
                onClick = { if (isEditing) selectedType = "Offer" },
                enabled = isEditing
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
        item{if (isEditing) {
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

        // UID clickable Card (only in viewing mode)
        item {
            if (!isEditing) {
                SectionHeader("Profile")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Pass the UID to the click handler
                        }
                        .padding(8.dp)
                ) {
                    Text(
                        text = post.uid,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Edit Toggle Button
        item{TextButton(
            onClick = {
                isEditing = !isEditing
                onEditToggle(isEditing)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Switch to View Mode" else "Switch to Edit Mode")
        }}
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
