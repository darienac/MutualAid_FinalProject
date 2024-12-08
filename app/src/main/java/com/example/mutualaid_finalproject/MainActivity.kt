package com.example.mutualaid_finalproject

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mutualaid_finalproject.model.MainViewModel
import com.example.mutualaid_finalproject.model.Post
import com.example.mutualaid_finalproject.model.ProfileTimeAvailability
import com.example.mutualaid_finalproject.ui.MyPostsScreen
import com.example.mutualaid_finalproject.ui.NewPostScreen
import com.example.mutualaid_finalproject.ui.PostSearchResult
import com.example.mutualaid_finalproject.ui.PostType
import com.example.mutualaid_finalproject.ui.ProfileScreen
import com.example.mutualaid_finalproject.ui.SearchScreen
import com.example.mutualaid_finalproject.ui.SettingsScreen
import com.example.mutualaid_finalproject.ui.SignInScreen
import com.example.mutualaid_finalproject.ui.theme.MutualAid_FinalProjectTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    val credentialManager = CredentialManager.create(this)
    val coroutineScope = lifecycleScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MutualAid_FinalProjectTheme {
                val owner = LocalViewModelStoreOwner.current

                owner?.let {
                    val viewModel: MainViewModel = viewModel(
                        it,
                        "MainViewModel",
                        MainViewModelFactory(
                            LocalContext.current.applicationContext as Application
                        )
                    )
                    MainNavigation(viewModel=viewModel, onLogin={email, password -> viewModel.handleSignIn(email, password, this)}, onSignup={email, password -> viewModel.handleSignUp(email, password, this)}, onGoogleLogin = {launchSignIn(viewModel)})
//                    DistanceCalculator("GET API KEY FROM GOOGLE DOC :3")
//                    TestDatabaseScreen(viewModel=viewModel)
                }
            }
        }
    }

    private fun launchSignIn(viewModel: MainViewModel) {
        val getPasswordOption = GetPasswordOption()

        // Get passkey from the user's public key credential provider.
//        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
//            requestJson = "requestJson"
//        )

//        val getCredRequest = GetCredentialRequest(
//            listOf(getPasswordOption, getPublicKeyCredentialOption)
//        )

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // probably set this to true for signing in, false if registering
            .setServerClientId("38412906080-ki73o7cfjddsq7b0jkbd638dcmreqt4d.apps.googleusercontent.com")
            .setAutoSelectEnabled(false)
//            .setNonce(<nonce string to use when generating a Google ID token>)
        .build()

        val getCredRequest: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    // Use an activity-based context to avoid undefined system UI
                    // launching behavior.
                    context = this@MainActivity,
                    request = getCredRequest
                )
                viewModel.handleSignIn(result, this@MainActivity)
            } catch (e : GetCredentialException) {
                Log.e("CredentialManager", e.errorMessage.toString())
                Toast.makeText(
                    this@MainActivity,
                    e.errorMessage.toString(),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}

@Composable
fun MainNavigation(viewModel: MainViewModel, onGoogleLogin: () -> Unit, onLogin: (String, String) -> Unit, onSignup: (String, String) -> Unit) { // Outermost composable where probably all/most of the UI logic can go
//    val postSearchResults = listOf(
//        PostSearchResult(
//            postId = "123",
//            type = PostType.REQUEST,
//            isAccepted = false,
//            title = "Math Tutor",
//            location = "Boston, MA"
//        ),
//        PostSearchResult(
//            postId = "124",
//            type = PostType.OFFER,
//            isAccepted = true,
//            title = "Grocery Delivery",
//            location = "Philadelphia, PA"
//        ),
//        PostSearchResult(
//            postId = "125",
//            type = PostType.REQUEST,
//            isAccepted = false,
//            title = "Dog Walker Needed",
//            location = "New York, NY"
//        )
//    )
    var postSearchResults = remember {mutableStateListOf<PostSearchResult>()}
    var selectedItem by remember {mutableIntStateOf(0)}
    val currentUser by viewModel.currentUser.observeAsState()
    val currentProfile by viewModel.profileRepository.currentProfile.collectAsState(null)

    if (currentUser == null || currentProfile == null) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Box(modifier=Modifier.padding(innerPadding)) {
                SignInScreen(onLogin=onLogin, onSignup=onSignup, onGoogleLogin=onGoogleLogin)
            }
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar() {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = {selectedItem = 0},
                    icon = {Icon(Icons.Filled.AccountCircle, "Profile")},
                    label = {Text("Profile")}
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = {selectedItem = 1},
                    icon = {Icon(Icons.Outlined.Info, "New Post")},
                    label = {Text("My Posts")}
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = {selectedItem = 2},
                    icon = {Icon(Icons.Filled.Search, "Search")},
                    label = {Text("Search")}
                )
                NavigationBarItem(
                    selected = selectedItem == 3,
                    onClick = {selectedItem = 3},
                    icon = {Icon(Icons.Filled.Settings, "Settings")},
                    label = {Text("Settings")}
                )
            }
        }
    ) { innerPadding ->
        Box(modifier=Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedItem) {
                0 -> ProfileScreen(
                    modifier=Modifier,
                    username=currentUser?.email ?: "",
                    name=currentProfile?.name ?: "",
                    description="Not yet in database schema",
                    skills=currentProfile?.skills ?: listOf(),
                    resources=currentProfile?.resources ?: listOf(),
                    availability=currentProfile?.daysAvailable ?: listOf(
                        ProfileTimeAvailability(false, false, false),
                        ProfileTimeAvailability(false, false, false),
                        ProfileTimeAvailability(false, false, false),
                        ProfileTimeAvailability(false, false, false),
                        ProfileTimeAvailability(false, false, false),
                        ProfileTimeAvailability(false, false, false),
                        ProfileTimeAvailability(false, false, false)),
                    onNameChange={ name->
                        currentProfile?.copy(name=name)?.let { viewModel.profileRepository.set(it, {}) }
                    },
                    onDescriptionChange={},
                    addSkill={ skill->
                        currentProfile?.copy(skills=currentProfile?.skills?.plus(skill) ?: listOf(skill))?.let { viewModel.profileRepository.set(it, {}) }
                    },
                    addResource={ resource->
                        currentProfile?.copy(resources=currentProfile?.resources?.plus(resource) ?: listOf(resource))?.let { viewModel.profileRepository.set(it, {}) }
                    },
                    changeAvailability={ index, time ->
                        var newAvailability = currentProfile?.daysAvailable?.toMutableList()
                        if (newAvailability != null) {
                            if (time == "Morning") {
                                newAvailability[index] = newAvailability[index].copy(morning=!newAvailability[index].morning)
                            } else if (time == "Afternoon") {
                                newAvailability[index] = newAvailability[index].copy(afternoon=!newAvailability[index].afternoon)
                            } else if (time == "Evening") {
                                newAvailability[index] = newAvailability[index].copy(evening=!newAvailability[index].evening)
                            }

                            currentProfile?.copy(daysAvailable = newAvailability)?.let { viewModel.profileRepository.set(it, {}) }
                        }
                    }
                )

                1 -> MyPostsScreen(
                    onNewPost = { type: String,
                                     username: String,
                                     title: String,
                                     description: String,
                                     imageUri: Uri?,
                                     location: String?,
                                     datePosted: String,
                                     dateLatest: String,
                                     tags: String ->
                        val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.US)
                        if (currentUser != null) {
                            val newPost = Post(
                                pid=java.util.UUID.randomUUID().toString(),
                                accepted=false,
                                date_expires=Timestamp(dateFormat.parse(dateLatest) ?: Date()),
                                date_posted=Timestamp(dateFormat.parse(datePosted) ?: Date()),
                                description=description,
                                location=location ?: "",
                                title=title,
                                type=if (type=="request") "request" else "offer",
                                uid=currentUser!!.uid
                            )
                            viewModel.postRepository.set(newPost, {})
                        }
                    },
                    uid = currentUser?.uid ?: ""
                )
                2 -> SearchScreen(modifier = Modifier, postSearchResults, onSearch = {query, maxDistance, selectedOption ->
                    viewModel.postRepository.search(query) {posts->
                        var newPostSearchResults = mutableListOf<PostSearchResult>() // Set all at once to avoid unnecessary recompositions
                        for (post in posts) {
                            newPostSearchResults.add(PostSearchResult(
                                postId=post.pid,
                                type=if (post.type == "request") PostType.REQUEST else PostType.OFFER,
                                isAccepted=post.accepted,
                                title=post.title,
                                location=post.location,
                                distance="Unknown"
                            ))
                        }
                        postSearchResults.clear()
                        postSearchResults.addAll(newPostSearchResults)
                        Log.d("SearchScreen", "updating with posts (${posts.size})")
                        for (postSearchResult in postSearchResults) {
                            if (postSearchResult.location == "") {
                                continue
                            }
                            viewModel.distanceCalculator.getDistanceAsync("BU CDS, Boston, MA", postSearchResult.location, postSearchResult.postId) {distance, pid->
                                Log.d("SearchScreen", "New distance (${postSearchResult.postId}) {${distance}}")
                                for (i in 0..postSearchResults.size-1) {
                                    if (postSearchResults[i].postId == pid) {
                                        postSearchResults[i] = postSearchResults[i].copy(distance=distance ?: "Unknown")
                                    }
                                }
                            }
                        }
                    }
                }, onPostClicked = {pid ->
                    Log.d("SearchScreen", "pid: $pid")
                })
                3 -> SettingsScreen(logout={
                    viewModel.logout()
                })
            }
        }
    }
}

class MainViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}

// Define the endpoints and parameters for the Google Maps Distance Matrix API.
interface DistanceMatrixService {
    @GET("distancematrix/json")
    suspend fun getDistance(
        @Query("origins") origins: String,
        @Query("destinations") destinations: String,
        @Query("mode") mode: String = "driving",
        @Query("key") apiKey: String,
        @Query("units") units: String = "imperial"    // Units: 'imperial' for miles, 'metric' for km
    ): DistanceMatrixResponse
}

// Define data classes to model the response from the API.
data class DistanceMatrixResponse(
    val rows: List<Row>
)

data class Row(
    val elements: List<Element>
)

data class Element(
    val distance: Distance,
    val duration: Duration,
    val status: String
)

data class Distance(val text: String, val value: Int)
data class Duration(val text: String, val value: Int)

@Composable
fun DistanceCalculator(apiKey: String) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = origin,
            onValueChange = { origin = it },
            label = { Text("Enter Origin") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Enter Destination") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                // Use the Retrofit interface to make API calls.
                // Call API to calculate distance
                CoroutineScope(Dispatchers.IO).launch {
                    val moshi = Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()

                    val service = Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/maps/api/")
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .build()
                        .create(DistanceMatrixService::class.java)

                    val response = service.getDistance(
                        origins = origin,
                        destinations = destination,
                        apiKey = apiKey
                    )

                    distance = response.rows.firstOrNull()
                        ?.elements?.firstOrNull()
                        ?.distance?.text
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Calculate Distance")
        }
        distance?.let {
            Text(
                text = "Distance: $it",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
