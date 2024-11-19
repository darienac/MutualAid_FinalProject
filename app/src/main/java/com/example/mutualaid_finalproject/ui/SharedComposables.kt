package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mutualaid_finalproject.R

@Composable
fun LocationInput(onSearch: (String) -> Unit
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
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.search_24dp_000000_fill0_wght400_grad0_opsz24), // Replace with your actual drawable resource
            contentDescription = "Search Icon",
            modifier = Modifier
                .size(24.dp)
                .clickable { onSearch(query) },
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
                color = Color.Black,
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

@Preview(showBackground = true)
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