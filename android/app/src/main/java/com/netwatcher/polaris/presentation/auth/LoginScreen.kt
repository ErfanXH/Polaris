package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.LoginRequest

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onNavigateToVerification: (numberOrEmail: String, password: String) -> Unit
) {
    val uiState by viewModel.authUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var numberOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var numberOrEmailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
        } else if (uiState is AuthUiState.Success) {
            snackbarHostState.showSnackbar("Login Successful", duration = SnackbarDuration.Short)
        }
    }

    val validateInputs: () -> Boolean = {
        var isValid = true

        numberOrEmailError = when {
            numberOrEmail.isBlank() -> {
                isValid = false
                "Email or phone is required"
            }
            else -> null
        }

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
                value = numberOrEmail,
                onValueChange = { numberOrEmail = it },
                label = { Text("Email or Phone") },
                isError = numberOrEmailError != null,
                modifier = Modifier.fillMaxWidth()
            )
            numberOrEmailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

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
                        viewModel.login(
                            LoginRequest(
                                numberOrEmail = numberOrEmail,
                                password = password
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign up")
            }

            LaunchedEffect(Unit) {
                when (uiState) {
                    is AuthUiState.Error -> snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
                    //AuthUiState.Success -> onNavigateToHome()
                    AuthUiState.RequiresVerification -> onNavigateToVerification(numberOrEmail, password)
                    else -> {}
                }
            }
        }
    }
}
