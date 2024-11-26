package com.example.mutualaid_finalproject.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onLogin: (String, String) -> Unit,
    onSignup: (String, String) -> Unit,
    onGoogleLogin: () -> Unit
) {
    val auth = remember { Firebase.auth}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        Spacer(Modifier.weight(1f))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = {
                onLogin(email, password)
                email = ""
                password = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
        TextButton(
            onClick = {
                onSignup(email, password)
                email = ""
                password = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Signup")
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onGoogleLogin,
            modifier = Modifier.fillMaxWidth(),
            colors=ButtonColors(Color.Black, Color.White, Color.Black, Color.White)
        ) {
            Text(text = "Sign in with Google", color=Color.White)
        }
    }
}