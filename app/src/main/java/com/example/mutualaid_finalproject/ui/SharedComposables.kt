package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mutualaid_finalproject.R

@Composable
fun LocationInput(maxDistance: Float, selectedOption: String, onSearch: (String, Float, String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.search_24dp_000000_fill0_wght400_grad0_opsz24), // Replace with your actual drawable resource
            contentDescription = "Search Icon",
            modifier = Modifier
                .size(24.dp)
                .clickable { onSearch(query, maxDistance, selectedOption) }, // CHANGE LATER TO GET INFO
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun MeetingAddressCard(
    locationName: String,
    address: String,
    onEditAddress: () -> Unit,
    onAddNote: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Meeting Address",
                style = MaterialTheme.typography.titleLarge,

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Location Name: \n$locationName",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Address: \n$address",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onEditAddress,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Edit Address",
                        color = Color(0xFF5DB075)
                    )
                }
                OutlinedButton(
                    onClick = onAddNote,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Add Note",
                        color = Color(0xFF5DB075)
                    )
                }
            }
        }
    }
}

@Composable
fun PostItem(postSearchResult: PostSearchResult, onPostClicked: (String) -> Unit) {
    // Display post details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPostClicked(postSearchResult.postId) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = postSearchResult.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (postSearchResult.distance != "") {
                Text(
                    text = "Distance: ${postSearchResult.distance}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnerScreen(title: String="", onClose: ()->Unit, content: @Composable ()->Unit) { // Container for a Composable app screen with an X button and title at top
    Column {
        CenterAlignedTopAppBar(
            windowInsets=WindowInsets(0.dp),
            title={
                Text(title, maxLines=1, overflow=TextOverflow.Ellipsis)
            },
            navigationIcon={
                IconButton(onClick=onClose) {
                    Icon(imageVector=Icons.AutoMirrored.Filled.ArrowBack,contentDescription="Previous Screen")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            )
        )
        Box(modifier=Modifier.weight(1f)) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicConfirmationDialog(title: String, desc: String, onConfirm: ()->Unit, onCancel: ()->Unit) {
    BasicAlertDialog(onDismissRequest=onCancel) {
        Card(
            shape=AlertDialogDefaults.shape,
            colors=CardColors(
                containerColor = AlertDialogDefaults.containerColor,
                contentColor = AlertDialogDefaults.textContentColor,
                disabledContainerColor = AlertDialogDefaults.containerColor,
                disabledContentColor = AlertDialogDefaults.textContentColor
            )
        ) {
            Column(modifier=Modifier.padding(24.dp)) {
                Text(title, fontSize=24.sp)
                Spacer(modifier=Modifier.height(16.dp))
                Text(desc)
                Spacer(modifier=Modifier.height(16.dp))
                Row {
                    Spacer(modifier=Modifier.weight(1f))
                    TextButton(onClick=onConfirm) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipList(allowEdits: Boolean = true, label: String, items: List<String>, onRemoveIndex: (Int)->Unit, onAddItem: (String)->Unit) {
    var newItemName by remember { mutableStateOf("") }

    Column {
        FlowRow {
            for (i in items.indices) {
                InputChip(
                    modifier=Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
                    onClick={if (allowEdits) {onRemoveIndex(i)}},
                    label={ Text(items[i]) },
                    selected=false,
                    trailingIcon= if (allowEdits) ({ Icon(Icons.Filled.Clear, "Remove: ${items[i]}") }) else null
                )
            }
        }
        if (allowEdits) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = {
                    onAddItem(newItemName)
                    newItemName = ""
                }) {
                    Text("Add")
                }
            }
        }
    }
}

@Composable
fun GreetingPreview() {
//    LocationInput(onSearch = { query ->
//        // Handle search logic here
//        println("Searching for: $query")
//    })
    MeetingAddressCard(
        "BU Faculty of Computing & Data Sciences",
        "665 Commonwealth Ave, Boston, MA 02215",
        onEditAddress = {
            // Handle Edit Address Logic
        },
        onAddNote = {
            // Handle Add Note Logic
        }
    )
}