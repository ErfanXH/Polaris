package com.netwatcher.polaris.presentation.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.domain.model.LoginRequest
import com.netwatcher.polaris.presentation.auth.AuthViewModel

@Composable
fun ResetPasswordStep(
    onSuccess: (String) -> Unit
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val focusManager = LocalFocusManager.current

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it },
        label = { Text("New Password") },
        isError = newPasswordError != null,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                Icon(
                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                )
            }
        }
    )
    newPasswordError?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it },
        label = { Text("Confirm Password") },
        isError = confirmPasswordError != null,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
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

            val (isValid, errors) = viewModel.validatePasswords(newPassword, confirmPassword)

            if (isValid) {
                newPasswordError = null
                confirmPasswordError = null
                focusManager.clearFocus(force = true)
                onSuccess(newPassword)
            } else {
                newPasswordError = errors["newPassword"]
                confirmPasswordError = errors["confirmPassword"]
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Reset Password")
    }
}