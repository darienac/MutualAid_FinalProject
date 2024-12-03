package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, logout: () -> Unit) {
    TextButton(
        onClick = {
            logout()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Logout")
    }
}