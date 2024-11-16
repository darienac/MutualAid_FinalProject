package com.example.mutualaid_finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mutualaid_finalproject.ui.ProfileScreen
import com.example.mutualaid_finalproject.ui.SearchScreen
import com.example.mutualaid_finalproject.ui.SettingsScreen
import com.example.mutualaid_finalproject.ui.theme.MutualAid_FinalProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MutualAid_FinalProjectTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() { // Outermost composable where probably all/most of the UI logic can go
    var selectedItem by remember {mutableIntStateOf(0)}
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
                    icon = {Icon(Icons.Filled.Search, "Search")},
                    label = {Text("Search")}
                )
                NavigationBarItem(
                    selected = selectedItem == 2,
                    onClick = {selectedItem = 2},
                    icon = {Icon(Icons.Filled.Settings, "Settings")},
                    label = {Text("Settings")}
                )
            }
        }
    ) { innerPadding ->
        Box(modifier=Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> ProfileScreen()
                1 -> SearchScreen()
                2 -> SettingsScreen()
            }
        }
    }
}