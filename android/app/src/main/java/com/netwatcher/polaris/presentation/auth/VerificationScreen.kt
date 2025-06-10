package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netwatcher.polaris.domain.model.VerificationRequest
import com.netwatcher.polaris.domain.model.VerificationRetryRequest
import kotlinx.coroutines.delay

enum class VerificationAction {
    VERIFY, RESEND
}

@Composable
fun VerificationScreen(
    viewModel: AuthViewModel,
    numberOrEmail: String,
    password: String,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    val uiState by viewModel.authUiState.collectAsState(initial = AuthUiState.Idle)
    val snackbarHostState = remember { SnackbarHostState() }
    var lastAction by remember { mutableStateOf<VerificationAction?>(null) }

    val focusManager = LocalFocusManager.current
    val focusRequesters = List(5) { remember { FocusRequester() } }

    var codeDigits = remember { mutableStateListOf("", "", "", "", "") }

    // Show error/success messages
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
                        onVerified()
                    }

                    VerificationAction.RESEND -> snackbarHostState.showSnackbar("Verification code resent")
                    null -> {}
                }
            }

            else -> {}
        }
    }

    val isCodeValid = codeDigits.all { it.length == 1 }
    val code = codeDigits.joinToString("")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter Verification Code", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isCodeValid) {
                        lastAction = VerificationAction.VERIFY
                        viewModel.verify(
                            VerificationRequest(
                                numberOrEmail = numberOrEmail,
                                password = password,
                                code = code
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isCodeValid
            ) {
                Text("Verify")
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                viewModel.retryVerification(VerificationRetryRequest(numberOrEmail))
            }) {
                lastAction = VerificationAction.RESEND
                Text("Resend Code")
            }

            TextButton(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}
