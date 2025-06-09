package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.ImeAction
import com.netwatcher.polaris.domain.model.SignUpRequest

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.authUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Show Snackbar on API error
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
        }
        /*else if (uiState is AuthUiState.Success) {
            snackbarHostState.showSnackbar()
        }*/
    }

    val validateInputs: () -> Boolean = {
        var isValid = true

        emailError = when {
            email.isBlank() -> {
                isValid = false
                "Email is required"
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                isValid = false
                "Invalid email format"
            }
            else -> null
        }

        phoneError = if (phoneNumber.isBlank()) {
            isValid = false
            "Phone number is required"
        } else null

        passwordError = when {
            password.isBlank() -> {
                isValid = false
                "Password is required"
            }
            password.length < 6 -> {
                isValid = false
                "Password must be at least 6 characters"
            }
            else -> null
        }

        isValid
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth()
            )
            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                isError = phoneError != null,
                modifier = Modifier.fillMaxWidth()
            )
            phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                isError = passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )
            passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (validateInputs()) {
                        viewModel.signUp(
                            SignUpRequest(
                                email = email,
                                phoneNumber = phoneNumber,
                                password = password
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            if (uiState is AuthUiState.Success) {
                LaunchedEffect(Unit) {
                    onSuccess()
                }
            }
        }
    }
}
