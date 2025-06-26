package com.colman.dailypulse

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
import com.colman.dailypulse.features.habits.SaveState // Assuming SaveState is reused from UserViewModel context [1]
import org.koin.androidx.compose.koinViewModel // If using Koin

@Composable
fun SignUpScreen(
    viewModel: UserViewModel = koinViewModel(),
    onSignUpSuccess: () -> Unit, // Callback for successful sign-up (might auto-sign-in then navigate)
    onNavigateToSignIn: () -> Unit // Callback to navigate back to sign-in screen
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Consider adding a 'confirmPassword' field for better UX
    // var confirmPassword by remember { mutableStateOf("") }

    val authState by viewModel.saveState.collectAsState() // Observe the shared state

    val snackbarHostState = remember { SnackbarHostState() }

    // LaunchedEffect to react to changes in authState
    LaunchedEffect(authState) {
        when (val currentAuthState = authState) {
            is SaveState.Success -> {
                // Important: If SaveState is used by both SignIn and SignUp,
                // ensure this success is relevant to the SignUp operation.
                // For a robust solution, consider distinct states or a way to tag operations.
                // Assuming for now that if we are on SignUpScreen, a Success means SignUp was successful.
                snackbarHostState.showSnackbar("Sign Up Successful! You can now sign in.")
//                onSignUpSuccess() // Navigate away (e.g., to sign-in or directly to main app)
                viewModel.resetSaveState() // Reset state to Idle
            }
            is SaveState.Error -> {
                snackbarHostState.showSnackbar("Error: ${currentAuthState.errorMessage}")
                viewModel.resetSaveState() // Reset state to Idle
            }
            else -> { /* Idle or Saving, do nothing for Snackbar here */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp), // Overall padding for the content
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

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
            // Optional: Add a Confirm Password field
            // Spacer(modifier = Modifier.height(16.dp))
            // OutlinedTextField(
            //     value = confirmPassword,
            //     onValueChange = { confirmPassword = it },
            //     label = { Text("Confirm Password") },
            //     singleLine = true,
            //     visualTransformation = PasswordVisualTransformation(),
            //     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            //     modifier = Modifier.fillMaxWidth()
            // )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Basic client-side validation (add more as needed)
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        // if (password == confirmPassword) { // If using confirmPassword
                        viewModel.onSignUp(email, password, name)
                        // } else {
                        //     scope.launch { snackbarHostState.showSnackbar("Passwords do not match.") }
                        // }
                    } else {
                        // Optionally show a local message or highlight empty fields
                        // For simplicity, relying on ViewModel/backend for detailed validation errors for now
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is SaveState.Saving // Disable button while saving
            ) {
                if (authState is SaveState.Saving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create Account")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToSignIn) {
                Text("Already have an account? Sign In")
            }
        }
    }
}