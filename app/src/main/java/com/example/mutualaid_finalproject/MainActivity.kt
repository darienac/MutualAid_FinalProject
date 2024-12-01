package com.example.mutualaid_finalproject

import android.app.Application
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
import com.example.mutualaid_finalproject.model.Profile
import com.example.mutualaid_finalproject.ui.NewPostScreen
import com.example.mutualaid_finalproject.ui.ProfileScreen
import com.example.mutualaid_finalproject.ui.SearchScreen
import com.example.mutualaid_finalproject.ui.SettingsScreen
import com.example.mutualaid_finalproject.ui.SignInScreen
import com.example.mutualaid_finalproject.ui.Time
import com.example.mutualaid_finalproject.ui.theme.MutualAid_FinalProjectTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



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
        topBar = {
            Text(currentProfile?.name ?: "No Name", modifier=Modifier.padding(8.dp, 16.dp))
        },
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
                    icon = {Icon(Icons.Outlined.AddCircle, "New Post")},
                    label = {Text("New Post")}
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
        Box(modifier=Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> ProfileScreen(
                    modifier=Modifier.padding(innerPadding),
                    username="username",
                    name="name",
                    description="I'm a cool guy!",
                    skills=listOf("sewing", "editing"),
                    resources=listOf("clothes", "food"),
                    availability=listOf(Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false))
                )
                1 -> NewPostScreen(postFunction={})
                2 -> SearchScreen()
                3 -> SettingsScreen()
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
                    val service = Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/maps/api/")
                        .addConverterFactory(GsonConverterFactory.create())
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
            Text(text = "Distance: $it", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
