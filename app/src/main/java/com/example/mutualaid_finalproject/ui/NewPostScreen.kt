import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    modifier: Modifier = Modifier,
    username: String,
    postFunction: (
        type: String,
        username: String,
        title: String,
        description: String,
        imageUri: Uri?,
        location: String?,
        datePosted: String,
        dateLatest: String,
        tags: String
    ) -> Unit
) {
    var type by remember { mutableStateOf("Offer/Request") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false)}
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Create New Post",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { /* Read-only field */ },
                label = { Text("Post Type") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Request") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange, // Replace with a relevant icon
                            contentDescription = "Request Icon"
                        )
                    },
                    onClick = {
                        type = "Request"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Offer") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange, // Replace with a relevant icon
                            contentDescription = "Offer Icon"
                        )
                    },
                    onClick = {
                        type = "Offer"
                        expanded = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Write your post details...") },
            modifier = Modifier.fillMaxWidth()
        )

        // Image Upload Section
        Text(text = "Upload Image", style = MaterialTheme.typography.titleMedium)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri == null) {
                TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Select Image")
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            placeholder = { Text("Optional: Enter location details") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = selectedDate?.let { convertMillisToDate(it) } ?: "",
            onValueChange = { },
            label = { Text("Expiration") },
            placeholder = { Text("MM/DD/YYYY") },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Select date")
            },
            modifier = modifier
                .fillMaxWidth()
                .pointerInput(selectedDate) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showModal = true
                        }
                    }
                }
        )

        if (showModal) {
            DatePickerModal(
                onDateSelected = { selectedDate = it },
                onDismiss = { showModal = false }
            )
        }

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags (comma-separated)") },
            placeholder = { Text("e.g., help, tutoring, food") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm")
                val current = LocalDateTime.now().format(formatter)
                val latest = if (selectedDate!=null)
                    convertMillisToDate(selectedDate!!).format(formatter)
                    else current // error message snackbar
                postFunction(
                    type,
                    username,
                    title,
                    description,
                    imageUri,
                    if (location.isNotBlank()) location else null,
                    current,
                    latest,
                    tags
                )
                type = ""
                title = ""
                description = ""
                location = ""
                selectedDate = null
                tags = ""
                imageUri = null
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && type.isNotBlank() && description.isNotBlank()
        ) {
            Text(text = "Create Post")
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun NewPostScreenPreview() {
    NewPostScreen(
        username="coolguy",
        postFunction = { type, username, title, description, imageUri, location, datePosted, dateLatest, tags ->
            println("Post created with:\nType: $type\nUsername: $username\nTitle: $title\nDescription: $description\nImage URI: $imageUri\nLocation: $location\nDate Posted: $datePosted\nDate Latest: $dateLatest\nTags: $tags")
        }
    )
}