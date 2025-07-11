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
import com.colman.dailypulse.features.habits.SaveState // Assuming SaveState is reused
import com.colman.dailypulse.utils.LocalSnackbarController
import org.koin.androidx.compose.koinViewModel // If using Koin
import com.colman.dailypulse.ui.components.AppHeader


@Composable
fun SignInScreen(
    viewModel: UserViewModel = koinViewModel(),
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.saveState.collectAsState()
    val snackbarController = LocalSnackbarController.current;

    LaunchedEffect(authState) {
        when (authState) {
            is SaveState.Success -> {
                snackbarController.showMessage("Sign In Successful!")
                onSignInSuccess()
                viewModel.resetSaveState()
            }
            is SaveState.Error -> {
                snackbarController.showMessage("Unable To Sign In")
                viewModel.resetSaveState()
            }
            else -> { /* Idle or Saving */ }
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
                    if (email.isNotBlank() && password.isNotBlank()) {
                        viewModel.onSignIn(email, password)
                    } else {
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is SaveState.Saving
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
