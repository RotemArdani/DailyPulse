package com.colman.dailypulse.features.auth

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
import com.colman.dailypulse.features.habits.SaveState
import com.colman.dailypulse.utils.LocalSnackbarController
import org.koin.androidx.compose.koinViewModel
import com.colman.dailypulse.ui.components.AppHeader

@Composable
fun SignUpScreen(
    viewModel: UserViewModel = koinViewModel(),
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.saveState.collectAsState()

    val snackbarController = LocalSnackbarController.current;

    LaunchedEffect(authState) {
        when (authState) {
            is SaveState.Success -> {
                snackbarController.showMessage("Sign Up Successful!")
                onSignUpSuccess()
                viewModel.resetSaveState()
            }
            else -> {
                snackbarController.showMessage("Unable To Sign Up")
                viewModel.resetSaveState()
            }
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppHeader()
            Spacer(modifier = Modifier.height(48.dp))
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
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        viewModel.onSignUp(email, password, name)
                    } else {
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is SaveState.Saving
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