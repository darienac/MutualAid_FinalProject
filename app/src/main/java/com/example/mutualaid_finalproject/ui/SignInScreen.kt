package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SignInScreen(modifier: Modifier = Modifier, onSignInRequest: () -> Unit) {
    Column(
        modifier = modifier
    ) {
        Button(onClick=onSignInRequest) {
            Text("Sign In")
        }
        Button(onClick=onSignInRequest) {
            Text("Register")
        }
    }
}