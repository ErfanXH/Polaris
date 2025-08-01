package com.netwatcher.polaris.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.hilt.navigation.compose.hiltViewModel
import com.netwatcher.polaris.presentation.home.components.DotsLoader

@Composable
fun SplashScreen(onTokenValidated: (Boolean) -> Unit) {
    val viewModel: AuthViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        val isAuthenticated = viewModel.isUserLoggedIn()
        onTokenValidated(isAuthenticated)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DotsLoader(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}