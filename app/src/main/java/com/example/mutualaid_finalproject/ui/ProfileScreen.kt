package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    username: String,
    name: String,
    description: String,
    skills: List<String>,
    resources: List<String>,
    availability: List<Time>,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    addSkill: (String) -> Unit,
    addResource: (String) -> Unit,
    changeAvailability: (Int, String) -> Unit
) {
    var editName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }
    var editDescription by remember { mutableStateOf(false) }
    var newDescription by remember { mutableStateOf(description) }
    var editSkills by remember { mutableStateOf(false) }
    var newSkill by remember { mutableStateOf("") }
    var editResources by remember { mutableStateOf(false) }
    var newResource by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Username Section
        Text(text = "Profile", modifier = Modifier.fillMaxWidth())
        Text(text = "Username: $username", modifier = Modifier.fillMaxWidth())

        // Name Section
        if (editName) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Edit Name") },
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = {
                    onNameChange(newName)
                    editName = false
                }) {
                    Text("Done")
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Name: $name", modifier = Modifier.weight(1f))
                TextButton(onClick = { editName = true }) {
                    Text("Edit")
                }
            }
        }

        // Description Section
        if (editDescription) {
            Column {
                OutlinedTextField(
                    value = newDescription,
                    onValueChange = { newDescription = it },
                    label = { Text("Edit Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = {
                    onDescriptionChange(newDescription)
                    editDescription = false
                }) {
                    Text("Done")
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Description: $description", modifier = Modifier.weight(1f))
                TextButton(onClick = { editDescription = true }) {
                    Text("Edit")
                }
            }
        }

        // Skills Section
        Text(text = "Skills", modifier = Modifier.fillMaxWidth())
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(skills) { skill ->
                Text(text = "- $skill")
            }
            item {
                if (editSkills) {
                    Row {
                        OutlinedTextField(
                            value = newSkill,
                            onValueChange = { newSkill = it },
                            label = { Text("New Skill") },
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = {
                            addSkill(newSkill)
                            newSkill = ""
                            editSkills = false
                        }) {
                            Text("Add")
                        }
                    }
                } else {
                    TextButton(onClick = { editSkills = true }) {
                        Text("Add Skill")
                    }
                }
            }
        }

        // Resources Section
        Text(text = "Resources", modifier = Modifier.fillMaxWidth())
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(resources) { resource ->
                Text(text = "- $resource")
            }
            item {
                if (editResources) {
                    Row {
                        OutlinedTextField(
                            value = newResource,
                            onValueChange = { newResource = it },
                            label = { Text("New Resource") },
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = {
                            addResource(newResource)
                            newResource = ""
                            editResources = false
                        }) {
                            Text("Add")
                        }
                    }
                } else {
                    TextButton(onClick = { editResources = true }) {
                        Text("Add Resource")
                    }
                }
            }
        }

        // Availability Section
        Text(text = "Availability", modifier = Modifier.fillMaxWidth())
        val days = listOf("Sun", "Mon", "Tues", "Weds", "Thurs", "Fri", "Sat")
        days.forEachIndexed { index, day ->
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = day, modifier = Modifier.weight(1f))
                listOf("Morning", "Afternoon", "Evening").forEach { time ->
                    TextButton(onClick = { changeAvailability(index, time) }) {
                        Text(text = time)
                    }
                }
            }
        }
    }
}

// Availability Data Class
class Time(
    val morning: Boolean,
    val afternoon: Boolean,
    val evening: Boolean
)

@Composable
@Preview
fun ProfileScreenPreview() {
    ProfileScreen(
        username = "test_user",
        name = "John Doe",
        description = "I am a friendly neighbor.",
        skills = listOf("Cooking", "Gardening"),
        resources = listOf("Lawn Mower", "Extra Seeds"),
        availability = List(7) { Time(false, false, false) },
        onNameChange = { newName -> println("Name changed to: $newName") },
        onDescriptionChange = { println("Description changed to: $it") },
        addSkill = { println("Added skill: $it") },
        addResource = { println("Added resource: $it") },
        changeAvailability = { day, time -> println("Changed $time availability for day: $day") }
    )
}