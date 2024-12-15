package com.example.mutualaid_finalproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mutualaid_finalproject.model.DistanceMatrixResponse
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mutualaid_finalproject.model.MainViewModel
import com.example.mutualaid_finalproject.model.Post
import com.example.mutualaid_finalproject.model.ProfileTimeAvailability
import com.example.mutualaid_finalproject.ui.MyPostsScreen
import com.example.mutualaid_finalproject.ui.PostSearchResult
import com.example.mutualaid_finalproject.ui.PostType
import com.example.mutualaid_finalproject.ui.ProfileScreen
import com.example.mutualaid_finalproject.ui.SearchScreen
import com.example.mutualaid_finalproject.ui.SettingsScreen
import com.example.mutualaid_finalproject.ui.SignInScreen
import com.example.mutualaid_finalproject.ui.theme.LogoPurple
import com.example.mutualaid_finalproject.ui.theme.MutualAid_FinalProjectTheme
import com.example.mutualaid_finalproject.ui.theme.OnLogo
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.ParseException
import android.Manifest
import androidx.compose.runtime.mutableStateMapOf
import com.example.mutualaid_finalproject.model.Profile


//private const val CHANNEL_ID = "post_acceptance_channel"
//private const val CHANNEL_NAME = "Post Acceptance Notifications"
//private const val CHANNEL_DESCRIPTION = "Notifications for accepted posts"


class MainActivity : ComponentActivity() {



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private var permissionGranted = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            permissionGranted = isGranted
        }

    val credentialManager = CredentialManager.create(this)
    val coroutineScope = lifecycleScope
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        // Check and request notification permission
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            val postNotificationPermission = "android.permission.POST_NOTIFICATIONS"
//            if (ContextCompat.checkSelfPermission(this, postNotificationPermission)
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(postNotificationPermission),
//                    100
//                )
//            }
//        }

//       fun requestLocationPermission() {
//            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//
//        // Check and request permission
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestLocationPermission()
//        }






        enableEdgeToEdge(
            statusBarStyle=SystemBarStyle.auto(
                LogoPurple.toArgb(),
                LogoPurple.toArgb()
            ),
            navigationBarStyle=SystemBarStyle.auto(
                LogoPurple.toArgb(),
                LogoPurple.toArgb()
            )
        )
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

val ANIMATION_DURATION = 200

@Composable
fun MainNavigation(viewModel: MainViewModel, onGoogleLogin: () -> Unit, onLogin: (String, String) -> Unit, onSignup: (String, String) -> Unit) { // Outermost composable where probably all/most of the UI logic can go
    var originLocation: String = ""
    var navController = rememberNavController()
    var navEntry = navController.currentBackStackEntryAsState()
    var postSearchResults = remember {mutableStateListOf<PostSearchResult>()}
    val currentUser by viewModel.currentUser.observeAsState()
    val currentProfile by viewModel.profileRepository.currentProfile.collectAsState(null)
    val profilesCache = remember {mutableStateMapOf<String, Profile>()}
    // Get a Context for notifications
    val context = LocalContext.current

    fun setLocation(location: String) {
        originLocation = location
        Log.d("MainActivity", "Origin location updated to: $originLocation")
    }
    val currentUserPosts by viewModel.postRepository.currentUserPosts.collectAsState(emptyList())

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

//    val expirationTimestamp = Timestamp(1734038949, 0)
//    val message = "Post is about to expire soon!"
//
//    viewModel.scheduleNotification(context, message, expirationTimestamp)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor=LogoPurple,
                contentColor=OnLogo,
            ) {
                val navBarColors = NavigationBarItemColors(
                    selectedIconColor=LogoPurple,
                    selectedTextColor=OnLogo,
                    selectedIndicatorColor=OnLogo,
                    unselectedIconColor=OnLogo,
                    unselectedTextColor=OnLogo,
                    disabledIconColor=OnLogo,
                    disabledTextColor=OnLogo
                )
                NavigationBarItem(
                    selected = navEntry.value?.destination?.route == "ProfileNav",
                    onClick = {navController.navigate("ProfileNav", navOptions=NavOptions.Builder().setRestoreState(true).build())},
                    icon = {Icon(Icons.Filled.AccountCircle, "Profile")},
                    label = {Text("Profile")},
                    colors=navBarColors
                )
                NavigationBarItem(
                    selected = navEntry.value?.destination?.route == "MyPostsNav",
                    onClick = {navController.navigate("MyPostsNav", navOptions=NavOptions.Builder().setRestoreState(true).build())},
                    icon = {Icon(Icons.Outlined.Info, "My Posts")},
                    label = {Text("My Posts")},
                    colors=navBarColors
                )
                NavigationBarItem(
                    selected = navEntry.value?.destination?.route == "SearchNav",
                    onClick = {navController.navigate("SearchNav", navOptions=NavOptions.Builder().setRestoreState(true).build())},
                    icon = {Icon(Icons.Filled.Search, "Search")},
                    label = {Text("Search")},
                    colors=navBarColors
                )
                NavigationBarItem(
                    selected = navEntry.value?.destination?.route == "SettingsNav",
                    onClick = {navController.navigate("SettingsNav", navOptions=NavOptions.Builder().setRestoreState(true).build())},
                    icon = {Icon(Icons.Filled.Settings, "Settings")},
                    label = {Text("Settings")},
                    colors=navBarColors
                )
            }
        }
    ) { innerPadding ->
        Box(modifier=Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController=navController,
                startDestination = "ProfileNav"
            ) {
                composable(
                    "ProfileNav",
                    enterTransition={
                        when(initialState.destination.route) {
                            "MyPostsNav", "SearchNav", "SettingsNav" ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                            else ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                        }
                    },
                    exitTransition={
                        when(targetState.destination.route) {
                            "MyPostsNav", "SearchNav", "SettingsNav" ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                            else ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                        }
                    }
                ) {
                    ProfileScreen(
                        modifier=Modifier,
                        email=currentUser?.email ?: "",
                        phoneNumber=currentProfile?.phoneNumber ?: "",
                        name=currentProfile?.name ?: "",
                        description=currentProfile?.description ?: "",
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
                        onPhoneNumberChange={ phoneNumber->
                            currentProfile?.copy(phoneNumber=phoneNumber)?.let { viewModel.profileRepository.set(it) {} }
                        },
                        onNameChange={ name->
                            currentProfile?.copy(name=name)?.let { viewModel.profileRepository.set(it) {} }
                        },
                        onDescriptionChange={ description->
                            currentProfile?.copy(description=description)?.let { viewModel.profileRepository.set(it) {} }
                        },
                        addSkill={ skill->
                            currentProfile?.copy(skills=currentProfile?.skills?.plus(skill) ?: listOf(skill))?.let { viewModel.profileRepository.set(it) {} }
                        },
                        removeSkill={ index->
                            currentProfile?.copy(skills=currentProfile?.skills?.filterIndexed {k,_-> k != index} ?: listOf())?.let { viewModel.profileRepository.set(it) {} }
                        },
                        addResource={ resource->
                            currentProfile?.copy(resources=currentProfile?.resources?.plus(resource) ?: listOf(resource))?.let { viewModel.profileRepository.set(it) {} }
                        },
                        removeResource={ index->
                            currentProfile?.copy(skills=currentProfile?.resources?.filterIndexed {k,_-> k != index} ?: listOf())?.let { viewModel.profileRepository.set(it) {} }
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
                }
                composable(
                    "MyPostsNav",
                    enterTransition={
                        when(initialState.destination.route) {
                            "SearchNav", "SettingsNav" ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                            else ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                        }
                    },
                    exitTransition={
                        when(targetState.destination.route) {
                            "SearchNav", "SettingsNav" ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                            else ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                        }
                    }
                ) {
                    MyPostsScreen(
                        onNewPost = { type: String,
                                      username: String,
                                      title: String,
                                      description: String,
                                      location: String?,
                                      datePosted: String,
                                      dateLatest: String,
                                      tags: String ->
                            val dateFormatPosted = SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.US)
                            val dateFormatLatest = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                            var timestampLatest: Timestamp? = null
                            var timestampPosted: Timestamp? = null
                            if (title == "") {
                                Toast.makeText(
                                    context,
                                    "No title given",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                return@MyPostsScreen
                            }
                            if (type != "Request" && type != "Offer") {
                                Toast.makeText(
                                    context,
                                    "Post must be either 'Request' or 'Offer'",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                return@MyPostsScreen
                            }
                            try {
                                timestampLatest = Timestamp(dateFormatLatest.parse(dateLatest) ?: Date())
                                timestampPosted = Timestamp(dateFormatPosted.parse(datePosted) ?: Date())
                            } catch (e: ParseException) {
                                Toast.makeText(
                                    context,
                                    "Invalid date(s) given",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                Log.d("NewPost", "Date Latest: $dateLatest")
                                Log.d("NewPost", "Date Posted: $datePosted")
                                return@MyPostsScreen
                            }
                            if (currentUser != null) {
                                val newPost = Post(
                                    pid =java.util.UUID.randomUUID().toString(),
                                    accepted =false,
                                    date_expires = timestampLatest,
                                    date_posted = timestampPosted,
                                    description = description,
                                    location = location ?: "",
                                    title = title,
                                    type = if (type=="Request") "request" else "offer",
                                    uid = currentUser!!.uid
                                )
                                viewModel.postRepository.set(newPost) {}
                                // create the notification with newPost.date_expires
//                                val expirationTimestamp = Timestamp(1734038949, 0)
                                val message = "Your post ${newPost.title} is about to expire soon!"
                                viewModel.scheduleNotification(context, message, newPost.date_expires)
                            }
                        },
                        uid = currentUser?.uid ?: "",
                        posts = currentUserPosts,
                        onPostEdit={viewModel.postRepository.set(it) {}},
                        onPostRemoved={viewModel.postRepository.delete(it) {}}
                    )
                }
                composable(
                    "SearchNav",
                    enterTransition={
                        when(initialState.destination.route) {
                            "SettingsNav" ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                            else ->
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                        }
                    },
                    exitTransition={
                        when(targetState.destination.route) {
                            "SettingsNav" ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                            else ->
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec=tween(ANIMATION_DURATION)
                                )
                        }
                    }
                ) {
                    SearchScreen(modifier = Modifier, postSearchResults, profilesCache, onSearch = {query, maxDistance, selectedOption ->
                            viewModel.postRepository.search(query) {posts->
                                var newPostSearchResults = mutableListOf<PostSearchResult>() // Set all at once to avoid unnecessary recompositions
                                profilesCache.clear()
                                var uids = mutableListOf<String>() // uids to query Profiles for
                                for (post in posts) {
                                    newPostSearchResults.add(PostSearchResult(
                                        postId=post.pid,
                                        date_expires=post.date_expires,
                                        date_posted=post.date_posted,
                                        description=post.description,
                                        type=if (post.type == "request") PostType.REQUEST else PostType.OFFER,
                                        uid=post.uid,
                                        isAccepted=post.accepted,
                                        title=post.title,
                                        location=post.location,
                                        distance="Unknown"
                                    ))
                                    uids.add(post.uid)
                                }
                                viewModel.profileRepository.getList(uids) {
                                    if (it != null) {
                                        profilesCache[it.uid] = it
                                    }
                                }
    //                            viewModel.distanceCalculator.getDistanceAsync(originLocation, postSearchResult.location, postSearchResult.postId) {distance, pid->
    //                                Log.d("SearchScreen", "New distance (${postSearchResult.postId}) {${distance}}")
    //                                // Sanitize the distance string by removing commas
    //                                val sanitizedDistance = distance?.replace(",", "") ?: "Unknown"
    //
    //                                for (i in 0 until postSearchResults.size) {
    //                                    if (postSearchResults[i].postId == pid) {
    //                                        postSearchResults[i] = postSearchResults[i].copy(distance = sanitizedDistance)
                                 postSearchResults.clear()
                                 postSearchResults.addAll(newPostSearchResults)
                                 Log.d("SearchScreen", "updating with posts (${posts.size})")
                                 for (postSearchResult in postSearchResults) {
                                     if (postSearchResult.location == "") {
                                         continue
                                     }
                                     viewModel.distanceCalculator.getDistanceAsync(originLocation, postSearchResult.location, postSearchResult.postId) {distance, pid->
    //                                     Log.d("SearchScreen", "New distance (${postSearchResult.postId}) {${distance}}")
    //                                     for (i in 0..postSearchResults.size-1) {
    //                                         if (postSearchResults[i].postId == pid) {
    //                                             postSearchResults[i] = postSearchResults[i].copy(distance=distance ?: "Unknown")
                                         Log.d("SearchScreen", "New distance (${postSearchResult.postId}) {${distance}}")
                                        // Sanitize the distance string by removing commas
                                        val sanitizedDistance = distance?.replace(",", "") ?: "Unknown"

                                        for (i in 0 until postSearchResults.size) {
                                            if (postSearchResults[i].postId == pid) {
                                                postSearchResults[i] = postSearchResults[i].copy(distance = sanitizedDistance)
                                             }
                                        }
                                    }

                                }
                            }
                        },
//                        onPostClicked = {
//                            val expirationTimestamp = Timestamp(1672502400000L, 0)
//                            val message = "Post is about to expire soon!"
//
//                            viewModel.scheduleNotification(context, message, expirationTimestamp)
//                        },
                        setLocation = { location -> setLocation(location) }
                    )
                }
                composable(
                    "SettingsNav",
                    enterTransition={
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec=tween(ANIMATION_DURATION)
                        )
                    },
                    exitTransition={
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec=tween(ANIMATION_DURATION)
                        )
                    }
                ) {
                    SettingsScreen(logout={
                        viewModel.logout()
                    })
                }
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