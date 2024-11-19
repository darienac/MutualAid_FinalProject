package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    Column() {
        LocationInput{}
        MeetingAddressCard(
            locationName = "Boston",
            address = "123 Commonwealth Avenue",
            onEditAddress={},
            onAddNote={}
        )
        Text(
            text = "Search Screen!",
            modifier = modifier
        )
    }
}