package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.R
import com.netwatcher.polaris.domain.model.VerificationRetryRequest
import com.netwatcher.polaris.presentation.home.components.DotsLoader

enum class VerificationAction {
    VERIFY, RESEND
}

@Composable
fun VerificationScreen(
    numberOrEmail: String,
    password: String,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val uiState by viewModel.authUiState.collectAsState(initial = AuthUiState.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var lastAction by remember { mutableStateOf<VerificationAction?>(null) }

    val focusManager = LocalFocusManager.current
    val focusRequesters = List(5) { remember { FocusRequester() } }

    var codeDigits = remember { mutableStateListOf("", "", "", "", "") }
    val code = codeDigits.joinToString("")

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Error -> snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
            is AuthUiState.Success -> {
                when (lastAction) {
                    VerificationAction.VERIFY -> {
                        snackbarHostState.showSnackbar(
                            message = "Verification successful",
                            duration = SnackbarDuration.Short
                        )
                        viewModel.resetState()
                        onVerified()
                    }

                    VerificationAction.RESEND -> snackbarHostState.showSnackbar("Verification code resent")
                    null -> {}
                }
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
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
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
            Text("Enter Verification Code", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                codeDigits.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            when {
                                newValue.length == 1 && newValue.all { it.isDigit() } -> {
                                    codeDigits[index] = newValue
                                    if (index < codeDigits.lastIndex) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }

                                newValue.isEmpty() && value.isNotEmpty() -> {
                                    codeDigits[index] = ""
                                    if (index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                }

                                newValue.isEmpty() -> {
                                    codeDigits[index] = ""
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var error = viewModel.validateVerificationCode(code)
                    if (error == null) {
                        focusManager.clearFocus(force = true)
                        lastAction = VerificationAction.VERIFY
                        viewModel.verify(numberOrEmail, password, code)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.validateVerificationCode(code) == null
            ) {
                Text("Verify")
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                DotsLoader()
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                lastAction = VerificationAction.RESEND
                viewModel.retryVerification(VerificationRetryRequest(numberOrEmail))
            }) {
                Text("Resend Code")
            }

            TextButton(onClick = {
                viewModel.resetState()
                onBack()
            }) {
                Text("Go Back")
            }
        }
    }
}
