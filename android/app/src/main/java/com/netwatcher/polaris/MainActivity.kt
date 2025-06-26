package com.netwatcher.polaris

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule
import com.netwatcher.polaris.presentation.auth.AuthViewModel
import com.netwatcher.polaris.presentation.auth.LoginScreen
import com.netwatcher.polaris.presentation.auth.SignUpScreen
import com.netwatcher.polaris.presentation.auth.VerificationScreen
import com.netwatcher.polaris.presentation.home.HomeScreen
import com.netwatcher.polaris.presentation.home.HomeViewModel
import com.netwatcher.polaris.presentation.theme.PolarisTheme
import com.netwatcher.polaris.utils.LocationUtility
import com.netwatcher.polaris.worker.NetworkMonitorWorker

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

    private var isWaitingForLocation = false
    private var isContentSet = false

    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        isWaitingForLocation = false
        checkLocationAndSetContent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
        } else {
            checkBatteryOptimizations() // <--- Add this
            NetworkMonitorWorker.schedule(this) // <--- Also make sure you reschedule here
            checkLocationAndSetContent()
        }
    }

    private fun checkLocationAndSetContent() {
        if (LocationUtility.isLocationEnabled(this)) {
            setAppContent()
        } else {
            showLocationEnableDialog()
        }
    }

    private fun showLocationEnableDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Required")
            .setMessage("Please enable location services to use this app")
            .setPositiveButton("Enable") { _, _ ->
                isWaitingForLocation = true
                LocationUtility.requestEnableLocation(this, locationSettingsLauncher)
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun setAppContent() {
        if (isContentSet) return
        isContentSet = true

        setContent {
            PolarisTheme {
                PolarisNav(this)
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

    private fun hasAllPermissions(): Boolean {
        val basePermissionsGranted = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundLocationGranted = REQUIRED_PERMISSIONS_API_29.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
            return basePermissionsGranted && backgroundLocationGranted
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return basePermissionsGranted
        }
        return true
    }

    private fun checkBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                checkBatteryOptimizations()
                NetworkMonitorWorker.schedule(this)
                checkLocationAndSetContent()
            } else {
                Toast.makeText(
                    this,
                    "Permissions are required for the app to function",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
}

@Composable
fun PolarisNav(mainActivity: MainActivity) {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(NetworkModule.authRepository) }

    val telephonyManager =
        mainActivity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val database = AppDatabaseHelper.getDatabase(mainActivity)
    val homeViewModel = remember {
        HomeViewModel(
            NetworkRepositoryImpl(
                context = mainActivity,
                telephonyManager = telephonyManager,
                networkDataDao = database.networkDataDao(),
                api = NetworkModule.networkDataApi
            )
        )
    }
    NavHost(navController = navController, startDestination = "login") {

        composable("sign_up") {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToVerification = { email, password ->
                    navController.navigate("verification?numberOrEmail=$email&password=$password")
                }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate("sign_up") },
                onNavigateToVerification = { numberOrEmail, password ->
                    navController.navigate("verification?numberOrEmail=$numberOrEmail&password=$password")
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
                viewModel = authViewModel,
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

        composable("home") {
            HomeScreen(viewModel = homeViewModel)
        }
    }
}
