package com.netwatcher.polaris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.netwatcher.polaris.presentation.auth.AuthViewModel
import com.netwatcher.polaris.presentation.auth.SignUpScreen
import com.netwatcher.polaris.presentation.theme.PolarisTheme
import com.netwatcher.polaris.data.repository.AuthRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = AuthViewModel(NetworkModule.authRepository)

        setContent {
            PolarisTheme {
                SignUpScreen(viewModel = viewModel) {
                    // onSuccess -> show message or navigate
                    println("Sign up success")
                }
            }
        }
    }
}
