package com.netwatcher.polaris.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import com.netwatcher.polaris.di.TokenManager
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun SplashScreen(onTokenValidated: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken().firstOrNull()
        onTokenValidated(!token.isNullOrEmpty())
    }

    // Optional loading UI while checking token
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}