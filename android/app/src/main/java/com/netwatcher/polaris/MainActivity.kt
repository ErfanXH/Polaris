package com.netwatcher.polaris

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.netwatcher.polaris.presentation.auth.LoginScreen
import com.netwatcher.polaris.presentation.auth.ResetPasswordScreen
import com.netwatcher.polaris.presentation.auth.SignUpScreen
import com.netwatcher.polaris.presentation.auth.SplashScreen
import com.netwatcher.polaris.presentation.auth.VerificationScreen
import com.netwatcher.polaris.presentation.home.HomeScreen
import com.netwatcher.polaris.presentation.permission.PermissionScreen
import com.netwatcher.polaris.presentation.settings.SettingsScreen
import com.netwatcher.polaris.presentation.theme.PolarisTheme
import com.netwatcher.polaris.utils.DataSyncScheduler
import com.netwatcher.polaris.utils.LocationUtility
import com.netwatcher.polaris.utils.TestAlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import com.netwatcher.polaris.utils.permission.PermissionManager
import com.netwatcher.polaris.utils.permission.PermissionManager.checkAndRequestExactAlarmPermission
import com.netwatcher.polaris.utils.permission.PermissionManager.checkBatteryOptimizations

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isWaitingForLocation = false
    private var isContentSet = false

    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isWaitingForLocation = false
            checkLocationAndSetContent()
        } else {
            Toast.makeText(this, "Location is required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val ungranted = PermissionManager.getUngrantedPermissions(
            this,
            PermissionManager.getInitialPermissions()
        )
        if (ungranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                ungranted.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            handlePostPermissionFlow()
        }
    }

    private fun handlePostPermissionFlow() {
        requestNotificationPermissionIfNeeded()
        requestBackgroundLocationIfNeeded()
        initializeApp()
    }

    private fun requestBackgroundLocationIfNeeded() {
        val perms = PermissionManager.getBackgroundLocationPermission()
        if (perms.isNotEmpty()) {
            val permission = perms.first()
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    BACKGROUND_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        val perms = PermissionManager.getNotificationPermission()
        if (perms.isNotEmpty()) {
            val permission = perms.first()
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun initializeApp() {
        checkBatteryOptimizations()
        checkAndRequestExactAlarmPermission()
        TestAlarmScheduler.scheduleTest(this)
        DataSyncScheduler.schedulePeriodicSync(this)
        checkLocationAndSetContent()
    }

    private fun checkLocationAndSetContent() {
        if (LocationUtility.isLocationEnabled(this)) {
            setAppContent()
        } else {
            promptEnableLocation()
        }
    }

    private fun promptEnableLocation() {
        val request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
            .setAlwaysShow(true)
            .build()

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(settingsRequest)

        task.addOnSuccessListener {
            checkLocationAndSetContent()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                locationSettingsLauncher.launch(intentSenderRequest)
            } else {
                Toast.makeText(this, "Unable to prompt location settings", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setAppContent() {
        if (isContentSet) return
        isContentSet = true
        setContent {
            PolarisTheme {
                PolarisNav()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isWaitingForLocation) {
            isWaitingForLocation = false
            checkLocationAndSetContent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (allGranted) {
                    handlePostPermissionFlow()
                } else {
                    Toast.makeText(
                        this,
                        "Some Required Permissions Denied. Go to Permissions Screen.",
                        Toast.LENGTH_LONG
                    ).show()
//                    finish()
                    handlePostPermissionFlow()
                }
            }

            BACKGROUND_PERMISSION_REQUEST_CODE,
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                Log.d("Permission", "Handled request code: $requestCode")
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val BACKGROUND_PERMISSION_REQUEST_CODE = 101
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 102
    }
}

@Composable
fun PolarisNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onTokenValidated = { isAuthenticated ->
                    navController.navigate(if (isAuthenticated) "home" else "login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("sign_up") {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToVerification = { email, password ->
                    navController.navigate("verification?numberOrEmail=$email&password=$password")
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("sign_up") },
                onNavigateToVerification = { numberOrEmail, password ->
                    navController.navigate("verification?numberOrEmail=$numberOrEmail&password=$password")
                },
                onNavigateToResetPassword = {
                    navController.navigate("reset_password")
                },
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable(
            "verification?numberOrEmail={numberOrEmail}&password={password}",
            arguments = listOf(
                navArgument("numberOrEmail") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val numberOrEmail = backStackEntry.arguments?.getString("numberOrEmail") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            VerificationScreen(
                numberOrEmail = numberOrEmail,
                password = password,
                onBack = { navController.popBackStack() },
                onVerified = {
                    navController.navigate("home") {
                        popUpTo("verification?numberOrEmail={numberOrEmail}&password={password}") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                onSuccess = {
                    navController.navigate("login") {
                        popUpTo("reset_password") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("reset_password") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onSettingsClick = { navController.navigate("settings") },
                onPermissionsClick = { navController.navigate("permissions") },
                onLogout = {
                    navController.navigate("login")
                },
            )
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("permissions") {
            PermissionScreen(navController)
        }
    }
}
