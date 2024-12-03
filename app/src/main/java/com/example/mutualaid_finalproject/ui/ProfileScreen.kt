package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mutualaid_finalproject.model.ProfileTimeAvailability

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    username: String,
    name: String,
    description: String,
    skills: List<String>,
    resources: List<String>,
    availability: List<ProfileTimeAvailability>,
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
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile Header
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Username Section
        Text(
            text = "Username: $username",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        // Name Section
        SectionHeader("Name")
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
                    Text("Save")
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
        SectionHeader("Description")
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
                    Text("Save")
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
        SectionHeader("Skills")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier=Modifier.height(128.dp).fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)) {
            items(skills) { skill ->
                Text(text = "- $skill", style = MaterialTheme.typography.bodyLarge)
            }
            item {
                if (editSkills) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
        SectionHeader("Resources")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier=Modifier.height(128.dp).fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)) {
            items(resources) { resource ->
                Text(text = "- $resource", style = MaterialTheme.typography.bodyLarge)
            }
            item {
                if (editResources) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
        SectionHeader("Availability")
        val days = listOf("Sun", "Mon", "Tues", "Weds", "Thurs", "Fri", "Sat")
        days.forEachIndexed { index, day ->
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = day, modifier = Modifier.weight(1f))
                listOf("Morning", "Afternoon", "Evening").forEach { time ->
                    var active =
                        (time == "Morning" && availability[index].morning) ||
                        (time == "Afternoon" && availability[index].afternoon) ||
                        (time == "Evening" && availability[index].evening)
                    if (active) {
                        FilledTonalButton(onClick = { changeAvailability(index, time) }) {
                            Text(text = time)
                        }
                    } else {
                        TextButton(onClick = { changeAvailability(index, time) }) {
                            Text(text = time)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth()
    )
}

// Preview
@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        username = "test_user",
        name = "John Doe",
        description = "I am a friendly neighbor.",
        skills = listOf("Cooking", "Gardening"),
        resources = listOf("Lawn Mower", "Extra Seeds"),
        availability = List(7) { ProfileTimeAvailability(false, false, false) },
        onNameChange = { println("Name changed to: $it") },
        onDescriptionChange = { println("Description changed to: $it") },
        addSkill = { println("Added skill: $it") },
        addResource = { println("Added resource: $it") },
        changeAvailability = { day, time -> println("Changed $time availability for day: $day") }
    )
}