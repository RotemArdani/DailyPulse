package com.colman.dailypulse.features.auth

// SignInScreen.kt
//package com.colman.dailypulse.features.user.ui // Or your preferred UI package

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.colman.dailypulse.features.user.UserViewModel
import com.colman.dailypulse.features.habits.SaveState // Assuming SaveState is reused
import org.koin.androidx.compose.koinViewModel // If using Koin

@Composable
fun SignInScreen(
    viewModel: UserViewModel = koinViewModel(),
    onSignInSuccess: () -> Unit, // Callback to navigate after successful sign-in
    onNavigateToSignUp: () -> Unit // Callback to navigate to sign-up screen
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.saveState.collectAsState() // Observe the state

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (authState) {
            is SaveState.Success -> {
                // Important: Check if this success was for THIS screen's operation.
                // If SaveState is shared, you might need more specific states.
                // For now, assume success means sign-in was successful.
                snackbarHostState.showSnackbar("Sign In Successful!")
                onSignInSuccess() // Navigate away
                viewModel.resetSaveState() // Reset state
            }
            is SaveState.Error -> {
                snackbarHostState.showSnackbar("Error: ${(authState as SaveState.Error).errorMessage}")
                viewModel.resetSaveState() // Reset state
            }
            else -> { /* Idle or Saving */ }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sign In", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Basic validation (add more as needed)
                    if (email.isNotBlank() && password.isNotBlank()) {
                        viewModel.onSignIn(email, password)
                    } else {
                        // Show local error or highlight fields
                        // For simplicity, let's assume ViewModel handles this if needed
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is SaveState.Saving // Disable button while saving
            ) {
                if (authState is SaveState.Saving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Sign In")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}