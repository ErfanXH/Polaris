package com.netwatcher.polaris.presentation.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.presentation.auth.AuthViewModel

@Composable
fun ResetVerificationStep(
    onSuccess: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val focusManager = LocalFocusManager.current
    val focusRequesters = List(5) { remember { FocusRequester() } }
    var codeDigits = remember { mutableStateListOf("", "", "", "", "") }
    val code = codeDigits.joinToString("")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
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

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            var error = viewModel.validateVerificationCode(code)
            if (error == null) {
                focusManager.clearFocus(force = true)
                onSuccess(code)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = viewModel.validateVerificationCode(code) == null
    ) {
        Text("Verify Code")
    }

    TextButton(
        onClick = {
            onRetry()
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Text("Resend Code")
    }
}
