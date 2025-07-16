package com.netwatcher.polaris

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.presentation.auth.AuthViewModel
import com.netwatcher.polaris.presentation.auth.LoginScreen
import com.netwatcher.polaris.presentation.auth.ResetPasswordScreen
import com.netwatcher.polaris.presentation.auth.SignUpScreen
import com.netwatcher.polaris.presentation.auth.SplashScreen
import com.netwatcher.polaris.presentation.auth.VerificationScreen
import com.netwatcher.polaris.presentation.home.HomeScreen
import com.netwatcher.polaris.presentation.home.HomeViewModel
import com.netwatcher.polaris.presentation.permissions.PermissionScreen
import com.netwatcher.polaris.presentation.settings.SettingsScreen
import com.netwatcher.polaris.presentation.settings.SettingsViewModel
import com.netwatcher.polaris.presentation.theme.PolarisTheme
import com.netwatcher.polaris.utils.DataSyncScheduler
import com.netwatcher.polaris.utils.LocationUtility
import com.netwatcher.polaris.utils.TestAlarmScheduler

class MainActivity : ComponentActivity() {

    private var isWaitingForLocation = false
    private var isContentSet = false

    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        isWaitingForLocation = false
        checkLocationAndSetContent()
    }

    /******************************************************/
//    val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            // Permission granted, continue with app initialization
//            if (hasAllPermissions()) {
//                initializeApp()
//            }
//        } else {
//            // Permission denied, show explanation or finish
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )) {
//                showPermissionRationaleDialog()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Permission required for app functionality",
//                    Toast.LENGTH_LONG
//                ).show()
//                finish()
//            }
//        }
//    }
//
//    private fun showPermissionRationaleDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("Permission Required")
//            .setMessage("This app needs location permission to function properly")
//            .setPositiveButton("Try Again") { _, _ ->
//                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//            }
//            .setNegativeButton("Exit") { _, _ -> finish() }
//            .show()
//    }
    /******************************************************/

    private fun checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
        } else {
            checkNotificationPermission()
        }
    }

    private fun initializeApp() {
        checkBatteryOptimizations()
        checkAndRequestExactAlarmPermission()
        // Schedule the first test and the periodic sync
        TestAlarmScheduler.scheduleTest(this)
        DataSyncScheduler.schedulePeriodicSync(this)
        checkLocationAndSetContent()
    }

    private fun checkLocationAndSetContent() {
        if (LocationUtility.isLocationEnabled(this)) {
            setAppContent()
        } else {
            showLocationEnableDialog()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS_API_33,
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                initializeApp() // Already granted, so initialize
            }
        } else {
            initializeApp() // Not needed for older APIs
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
        }
        return basePermissionsGranted
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
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    checkNotificationPermission()
                } else {
                    Toast.makeText(
                        this,
                        "Permissions are required for the app to function",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }

            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                initializeApp()
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 1
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
        )

        @RequiresApi(Build.VERSION_CODES.Q)
        private val REQUIRED_PERMISSIONS_API_29 = arrayOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val REQUIRED_PERMISSIONS_API_33 = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}

@Composable
fun PolarisNav(mainActivity: MainActivity) {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(NetworkModule.authRepository) }

    val database = AppDatabaseHelper.getDatabase(mainActivity)

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            repository = NetworkRepositoryImpl(
                context = mainActivity.applicationContext,
                networkDataDao = database.networkDataDao(),
                api = NetworkModule.networkDataApi
            ),
            application = mainActivity.application
        )
    )

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(mainActivity.application) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onTokenValidated = { isLoggedIn ->
                    navController.navigate(if (isLoggedIn) "home" else "login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

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
        composable("reset_password") {
            ResetPasswordScreen(
                viewModel = authViewModel,
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
                viewModel = homeViewModel,
                onSettingsClick = { navController.navigate("settings") },
                onPermissionsClick = {navController.navigate("permissions")},
                onLogout = {
                    navController.navigate("login")
                },
                context = mainActivity
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onSimSelected = { simSlotId, simSubsId ->
                    homeViewModel.setSelectedSim(simSlotId, simSubsId)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("permissions") {
//            PermissionScreen(
//                onBackClick = { navController.popBackStack() },
//                onRequestPermission = { permission ->
//                    mainActivity.requestPermissionLauncher.launch(permission)
//                },
//                onOpenSettings = { intentAction ->
//                    intentAction?.let {
//                        val intent = Intent(it)
//                        mainActivity.startActivity(intent)
//                    }
//                }
//            )
            PermissionScreen(navController, mainActivity)
        }
    }
}

class HomeViewModelFactory(
    private val repository: NetworkRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
