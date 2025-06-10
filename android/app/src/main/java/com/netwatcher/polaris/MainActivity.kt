package com.netwatcher.polaris

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.netwatcher.polaris.presentation.auth.AuthViewModel
import com.netwatcher.polaris.presentation.auth.SignUpScreen
import com.netwatcher.polaris.presentation.theme.PolarisTheme
import androidx.compose.runtime.*
import com.netwatcher.polaris.data.repository.AuthRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.netwatcher.polaris.presentation.auth.LoginScreen
import com.netwatcher.polaris.presentation.auth.VerificationScreen
import com.netwatcher.polaris.presentation.home.HomeScreen


class MainActivity : ComponentActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        private val REQUIRED_PERMISSIONS_API_29 = arrayOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PolarisTheme {
                PolarisNav()
            }
        }
    }
}

@Composable
fun PolarisNav() {
    val navController = rememberNavController()
    val viewModel = AuthViewModel(NetworkModule.authRepository)

    NavHost(navController = navController, startDestination = "login") {

        composable("sign_up") {
            SignUpScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToVerification = { email, password ->
                    navController.navigate("verification?numberOrEmail=$email&password=$password")
                },
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToSignUp = { navController.navigate("sign_up") },
                onNavigateToVerification = { numberOrEmail, password ->
                    navController.navigate("verification?numberOrEmail=$numberOrEmail&password=$password")
                },
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("verification?numberOrEmail={numberOrEmail}&password={password}",
            arguments = listOf(
                navArgument("numberOrEmail") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )) { backStackEntry ->
            val numberOrEmail = backStackEntry.arguments?.getString("numberOrEmail") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            VerificationScreen(
                viewModel = viewModel,
                numberOrEmail = numberOrEmail,
                password = password,
                onBack = { navController.popBackStack() },
                onVerified = { navController.navigate("home") })
        }

        composable("home") {

        }
    }
}

