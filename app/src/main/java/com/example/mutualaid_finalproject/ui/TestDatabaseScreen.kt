package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mutualaid_finalproject.model.MainViewModel

@Composable
fun TestDatabaseScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    var username by rememberSaveable {mutableStateOf("")}

    Column(modifier=modifier) {
        Spacer(modifier=Modifier.height(16.dp))
        Row {
            OutlinedTextField(username, {username = it}, label={Text("Username")})
            Button(onClick={viewModel.createNewUser(username)}) {
                Text("New User")
            }
        }
    }
}