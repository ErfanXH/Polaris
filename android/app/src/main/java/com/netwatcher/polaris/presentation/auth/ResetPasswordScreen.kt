@file:OptIn(ExperimentalMaterial3Api::class)

package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.R
import com.netwatcher.polaris.domain.model.ResetPasswordRequest
import com.netwatcher.polaris.domain.model.VerifyResetCodeRequest
import com.netwatcher.polaris.presentation.auth.AuthUiState
import com.netwatcher.polaris.presentation.auth.AuthViewModel

@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.authUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentStep by remember { mutableStateOf(ResetStep.IDENTIFIER) }

    var identifier by remember { mutableStateOf("") }
    var codeDigits = remember { mutableStateListOf("", "", "", "", "") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val focusRequesters = List(5) { remember { FocusRequester() } }

    val isCodeValid = codeDigits.all { it.length == 1 }
    val code = codeDigits.joinToString("")

    var identifierError by remember { mutableStateOf<String?>(null) }
    var codeError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Error -> snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
            AuthUiState.CodeSent -> {
                currentStep = ResetStep.VERIFICATION
                snackbarHostState.showSnackbar("Reset code sent")
                viewModel.resetState()
            }

            AuthUiState.CodeVerified -> {
                currentStep = ResetStep.NEW_PASSWORD
                snackbarHostState.showSnackbar("Reset code verified")
                viewModel.resetState()
            }

            AuthUiState.Success -> {
                snackbarHostState.showSnackbar("Password reset successful")
                viewModel.resetState()
                onSuccess()
            }

            AuthUiState.RequiresVerification -> currentStep = ResetStep.VERIFICATION
            AuthUiState.Loading -> {}
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
                SnackbarHost(hostState = snackbarHostState)
            }
        },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        currentStep = when (currentStep) {
                            ResetStep.NEW_PASSWORD -> ResetStep.VERIFICATION
                            ResetStep.VERIFICATION -> ResetStep.IDENTIFIER
                            ResetStep.IDENTIFIER -> {
                                onBackToLogin()
                                return@IconButton
                            }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 24.dp),
            )

            // 🔻 Dynamic Title
            Text(
                text = when (currentStep) {
                    ResetStep.IDENTIFIER -> "Reset Password"
                    ResetStep.VERIFICATION -> "Enter Verification Code"
                    ResetStep.NEW_PASSWORD -> "Set New Password"
                },
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔻 Form Logic
            when (currentStep) {
                ResetStep.IDENTIFIER -> {
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Email or Phone") },
                        isError = identifierError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    identifierError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (identifier.isBlank()) {
                                identifierError = "Please enter a valid email or phone"
                            } else {
                                identifierError = null
                                focusManager.clearFocus()
                                viewModel.sendResetCode(identifier)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Code")
                    }
                }

                ResetStep.VERIFICATION -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        codeDigits.forEachIndexed { index, value ->
                            OutlinedTextField(
                                value = value,
                                onValueChange = {
                                    if (it.length <= 1 && it.all { c -> c.isDigit() }) {
                                        codeDigits[index] = it
                                        if (it.isNotEmpty() && index < 4) {
                                            focusRequesters[index + 1].requestFocus()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(48.dp)
                                    .height(64.dp)
                                    .focusRequester(focusRequesters[index]),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isCodeValid) {
                                focusManager.clearFocus(force = true)
                                viewModel.verifyResetCode(VerifyResetCodeRequest(identifier, code))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isCodeValid
                    ) {
                        Text("Verify Code")
                    }
                }

                ResetStep.NEW_PASSWORD -> {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        isError = passwordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                    passwordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        isError = confirmPasswordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                    confirmPasswordError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            when {
                                newPassword.length < 6 -> {
                                    passwordError = "Password must be at least 6 characters"
                                    confirmPasswordError = null
                                }

                                newPassword != confirmPassword -> {
                                    passwordError = null
                                    confirmPasswordError = "Passwords do not match"
                                }

                                else -> {
                                    passwordError = null
                                    confirmPasswordError = null
                                    focusManager.clearFocus()
                                    viewModel.resetPassword(
                                        ResetPasswordRequest(identifier, code, newPassword)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset Password")
                    }
                }
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}


enum class ResetStep {
    IDENTIFIER,
    VERIFICATION,
    NEW_PASSWORD
}