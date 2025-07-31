@file:OptIn(ExperimentalMaterial3Api::class)

package com.netwatcher.polaris.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.R
import com.netwatcher.polaris.presentation.auth.components.ResetIdentifierStep
import com.netwatcher.polaris.presentation.auth.components.ResetPasswordStep
import com.netwatcher.polaris.presentation.auth.components.ResetVerificationStep

@Composable
fun ResetPasswordScreen(
    onSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val uiState by viewModel.authUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentStep by remember { mutableStateOf(ResetStep.IDENTIFIER) }

    var identifier = remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

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

            AuthUiState.RequiresVerification -> {
                viewModel.resetState()
                currentStep = ResetStep.VERIFICATION
            }

            AuthUiState.Loading -> {}
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                    .wrapContentSize(Alignment.TopCenter)
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
                            ResetStep.NEW_PASSWORD -> {
                                ResetStep.VERIFICATION
                            }

                            ResetStep.VERIFICATION -> {
                                ResetStep.IDENTIFIER
                            }

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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
        ) {
            val screenHeight = maxHeight
            val contentHeight = 480.dp // estimated height of your form content
            val topPadding = ((screenHeight - contentHeight) / 2).coerceAtLeast(0.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = topPadding, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .height(100.dp)
                        .padding(bottom = 24.dp),
                )

                Text(
                    text = when (currentStep) {
                        ResetStep.IDENTIFIER -> "Reset Password"
                        ResetStep.VERIFICATION -> "Enter Verification Code"
                        ResetStep.NEW_PASSWORD -> "Set New Password"
                    },
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (currentStep) {
                    ResetStep.IDENTIFIER -> {
                        ResetIdentifierStep(
                            identifier = identifier,
                            onSuccess = { id ->
                                viewModel.sendResetCode(id)
                            })
                    }

                    ResetStep.VERIFICATION -> {
                        ResetVerificationStep(onSuccess = { otp ->
                            code = otp
                            viewModel.verifyResetCode(
                                identifier.value,
                                code
                            )
                        }, onRetry = { viewModel.sendResetCode(identifier.value) })
                    }

                    ResetStep.NEW_PASSWORD -> {
                        ResetPasswordStep(onSuccess = { password ->
                            viewModel.resetPassword(
                                identifier.value,
                                code,
                                password
                            )
                        })
                    }
                }

                if (uiState is AuthUiState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }

        }
    }
}


enum class ResetStep {
    IDENTIFIER,
    VERIFICATION,
    NEW_PASSWORD
}