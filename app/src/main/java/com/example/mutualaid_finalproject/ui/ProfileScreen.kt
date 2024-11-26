package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, username: String, name: String,
                  description: String, skills: List<String>, resources: List<String>,
                  availability: List<Time>) {
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile",
            modifier = modifier
        )
        Text (
            text = username,
            modifier = modifier
        )
        Text (
            text = name,
            modifier = modifier
        )
        Text (
            text = description,
            modifier = modifier
        )
        Row (
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            LazyColumn {
                item {
                    Text(text = "Skills")
                }
                items(skills.size) { index ->
                    Text(text = skills[index])
                }
            }
            LazyColumn {
                item {
                    Text(text = "Resources")
                }
                items(resources.size) { index ->
                    Text(text = resources[index])
                }
            }
        }
    }
}

class Time(val morning: Boolean, val afternoon: Boolean, val evening: Boolean)

@Composable
@Preview
fun ProfileScreenPreview() {
    ProfileScreen(
        modifier=Modifier,
        username="username",
        name="name",
        description="I'm a cool guy!",
        skills=listOf("sewing", "editing"),
        resources=listOf("clothes", "food"),
        availability = listOf(Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false), Time(false, false, false))
    )
}