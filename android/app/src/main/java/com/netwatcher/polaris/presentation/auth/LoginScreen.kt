package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.R
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.presentation.home.components.DotsLoader

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToVerification: (numberOrEmail: String, password: String) -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onSuccess: () -> Unit
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val uiState by viewModel.authUiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var numberOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var numberOrEmailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
                viewModel.resetState()
            }

            AuthUiState.Success -> {
                snackbarHostState.showSnackbar(
                    "Login Successful",
                    duration = SnackbarDuration.Short
                )
                viewModel.resetState()
                onSuccess()
            }

            AuthUiState.RequiresVerification -> {
                viewModel.resetState()
                onNavigateToVerification(numberOrEmail, password)
            }

            else -> {}
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = {
                    onNavigateToResetPassword()
                }) {
                    Text("Forgot Password?")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val (isValid, errors) = viewModel.validateLoginInputs(numberOrEmail, password)

                    if (isValid) {
                        numberOrEmailError = null
                        passwordError = null
                        focusManager.clearFocus(force = true)
                        viewModel.login(LoginRequest(numberOrEmail, password))
                    } else {
                        numberOrEmailError = errors["numberOrEmail"]
                        passwordError = errors["password"]
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToSignUp) {
                Text("Don't have an account? Sign up")
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                DotsLoader()
            }
        }
    }
}
