package com.netwatcher.polaris.presentation.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.presentation.auth.AuthViewModel

@Composable
fun ResetIdentifierStep(
    identifier: MutableState<String>,
    onSuccess: (String) -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val focusManager = LocalFocusManager.current
    var identifierError by remember { mutableStateOf<String?>(null) }

    OutlinedTextField(
        value = identifier.value,
        onValueChange = { identifier.value = it },
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
            var error = viewModel.validateIdentifier(identifier.value)
            if (error != null) {
                identifierError = error
            } else {
                identifierError = null
                focusManager.clearFocus()
                onSuccess(identifier.value)
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Send Code")
    }
}