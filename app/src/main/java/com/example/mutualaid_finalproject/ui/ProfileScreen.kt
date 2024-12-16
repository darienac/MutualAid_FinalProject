package com.example.mutualaid_finalproject.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mutualaid_finalproject.R
import com.example.mutualaid_finalproject.model.ProfileTimeAvailability
import com.example.mutualaid_finalproject.ui.theme.MutualAid_FinalProjectTheme

fun getPhoneNumberUri(phoneNumber: String, scheme: String): Uri? {
    var digits = phoneNumber.filter {it.isDigit()}
    if (digits.length > 11 || digits.length < 10) {
        return null
    }
    if (digits.length == 10) {
        digits = "1$digits"
    }
    return Uri.parse("$scheme:$digits")
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    allowEdits: Boolean = true,
    email: String,
    phoneNumber: String,
    name: String,
    description: String,
    skills: List<String>,
    resources: List<String>,
    availability: List<ProfileTimeAvailability>,
    onPhoneNumberChange: (String) -> Unit={},
    onNameChange: (String) -> Unit={},
    onDescriptionChange: (String) -> Unit={},
    addSkill: (String) -> Unit={},
    removeSkill: (Int) -> Unit={},
    addResource: (String) -> Unit={},
    removeResource: (Int) -> Unit={},
    changeAvailability: (Int, String) -> Unit={_,_->}
) {
    var editPhoneNumber by remember { mutableStateOf(false) }
    var newPhoneNumber by remember { mutableStateOf(phoneNumber) }
    var editName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }
    var editDescription by remember { mutableStateOf(false) }
    var newDescription by remember { mutableStateOf(description) }
    var editSkills by remember { mutableStateOf(false) }
    var newSkill by remember { mutableStateOf("") }
    var editResources by remember { mutableStateOf(false) }
    var newResource by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile Header
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Name Section
        SectionHeader("Name")
        if (editName) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Edit Name") },
                    modifier = Modifier.weight(1f).testTag("name_input")
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
                Text(text = name, modifier = Modifier.weight(1f))
                if (allowEdits) {
                    TextButton(onClick = { editName = true }, modifier=Modifier.testTag("name_enable_input")) {
                        Text("Edit")
                    }
                }
            }
        }

        // Contact Section
        Row(verticalAlignment=Alignment.CenterVertically) {
            SectionHeader("Contact Info")
            Spacer(modifier=Modifier.weight(1f))
            val telUri = getPhoneNumberUri(phoneNumber, "tel")
            val smsUri = getPhoneNumberUri(phoneNumber, "sms")
            if (telUri != null) {
                IconButton(onClick={
                    val intent = Intent(Intent.ACTION_DIAL, telUri)
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.Call, "Call", tint=MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick={
                    val intent = Intent(Intent.ACTION_SENDTO, smsUri)
                    context.startActivity(intent)
                }) {
                    Icon(painterResource(R.drawable.baseline_chat_24), "Text", tint= MaterialTheme.colorScheme.primary)
                }
            }
            if (email != "") {
                IconButton(onClick={
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    intent.setType("message/rfc822")
//                    context.startActivity(Intent.createChooser(intent, "Choose an Email client:"))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.Email, "Email", tint=MaterialTheme.colorScheme.primary)
                }
            }
        }
        Text(
            text = "Email: $email",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().testTag("email_input")
        )

        Row(verticalAlignment=Alignment.CenterVertically) {
            if (editPhoneNumber) {
                OutlinedTextField(
                    value = newPhoneNumber,
                    onValueChange = { newPhoneNumber = it },
                    label = { Text("Edit Phone Number") },
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = {
                    onPhoneNumberChange(newPhoneNumber)
                    editPhoneNumber = false
                }) {
                    Text("Save")
                }
            } else {
                Text(text = "Phone Number: $phoneNumber", modifier = Modifier.weight(1f))
                if (allowEdits) {
                    TextButton(onClick = { editPhoneNumber = true }) {
                        Text("Edit")
                    }
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
                Text(text = description, modifier = Modifier.weight(1f))
                if (allowEdits) {
                    TextButton(onClick = { editDescription = true }) {
                        Text("Edit")
                    }
                }
            }
        }

        // Skills Section
        SectionHeader("Skills")
        ChipList(allowEdits, "New Skill", skills, onRemoveIndex=removeSkill, onAddItem=addSkill)

        // Resources Section
        SectionHeader("Resources")
        ChipList(allowEdits, "New Resource", resources, onRemoveIndex=removeResource, onAddItem=addResource)

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
                        if (allowEdits) {
                            FilledTonalButton(enabled=allowEdits, onClick = { changeAvailability(index, time) }) {
                                Text(text = time)
                            }
                        } else {
                            Text(time, modifier=Modifier.padding(16.dp))
                        }
                    } else if (allowEdits) {
                        TextButton(enabled=allowEdits, onClick = { changeAvailability(index, time) }) {
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
        fontWeight = FontWeight.Bold
    )
}

// Preview
@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        email = "test_user",
        phoneNumber = "",
        name = "John Doe",
        description = "I am a friendly neighbor.",
        skills = listOf("Cooking", "Gardening"),
        resources = listOf("Lawn Mower", "Extra Seeds"),
        availability = List(7) { ProfileTimeAvailability(false, false, false) },
        onPhoneNumberChange = { println("Phone number changed to: $it") },
        onNameChange = { println("Name changed to: $it") },
        onDescriptionChange = { println("Description changed to: $it") },
        addSkill = { println("Added skill: $it") },
        removeSkill = { println("Removed skill: $it") },
        addResource = { println("Added resource: $it") },
        removeResource = { println("Removed resource: $it") },
        changeAvailability = { day, time -> println("Changed $time availability for day: $day") }
    )
}