package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import com.netwatcher.polaris.R
import com.netwatcher.polaris.domain.model.SignUpRequest

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToVerification: (email: String, password: String) -> Unit
) {
    val uiState by viewModel.authUiState.collectAsState(initial = AuthUiState.Idle)
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Show Snack bar on API error
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
        } else if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onNavigateToVerification(email, password)
        }
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

        phoneError = when {
            phoneNumber.isBlank() -> {
                isValid = false
                "Phone number is required"
            }

            !phoneNumber.startsWith("09") -> {
                isValid = false
                "Phone number should start with '09'"
            }

            phoneNumber.length != 11 -> {
                isValid = false
                "Phone number should be 11 digits"
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
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp) // Adjust as needed
                    .wrapContentSize(Alignment.TopCenter) // 👈 Align top
            ) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 24.dp),
            )
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
                        focusManager.clearFocus(force = true)
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
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                viewModel.resetState()
                onNavigateToLogin()
            }) {
                Text("Already have an account? Login")
            }
        }
    }
}
