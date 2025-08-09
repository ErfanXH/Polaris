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
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.R
import com.netwatcher.polaris.presentation.home.components.DotsLoader

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToVerification: (email: String, password: String) -> Unit
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val uiState by viewModel.authUiState.collectAsState(initial = AuthUiState.Idle)
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
            viewModel.resetState()
        } else if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onNavigateToVerification(email, password)
        }
    }

    Scaffold(
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp) // Adjust as needed
                    .wrapContentSize(Alignment.TopCenter) // 👈 Align top
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { snackbarData ->
                        Snackbar(
                            snackbarData = snackbarData,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    }
                )
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
                isError = phoneNumberError != null,
                modifier = Modifier.fillMaxWidth()
            )
            phoneNumberError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

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
                    val (isValid, errors) = viewModel.validateSignUpInputs(
                        email,
                        phoneNumber,
                        password
                    )
                    if (isValid) {
                        focusManager.clearFocus(force = true)
                        viewModel.signUp(email, phoneNumber, password)
                    } else {
                        emailError = errors["email"]
                        phoneNumberError = errors["phoneNumber"]
                        passwordError = errors["password"]
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                DotsLoader()
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
