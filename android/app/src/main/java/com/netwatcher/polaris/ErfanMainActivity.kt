package com.netwatcher.polaris

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.presentation.home.HomeScreen
import com.netwatcher.polaris.presentation.home.HomeViewModel
import com.netwatcher.polaris.presentation.theme.*
import com.netwatcher.polaris.utils.LocationUtility

class ErfanMainActivity : ComponentActivity() {
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

    private val viewModel: HomeViewModel by viewModels {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        HomeViewModelFactory(
            NetworkRepositoryImpl(
                context = this,
                telephonyManager = telephonyManager,
                connectivityManager = connectivityManager
            )
        )
    }

    private var isWaitingForLocation = false

    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isWaitingForLocation = false
        checkLocationAndSetContent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isWaitingForLocation = false

        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
        } else {
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
            HomePageTheme {
                HomeScreen(viewModel = viewModel)
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

    private var isContentSet = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("PermissionResult", "Received permission results")

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("PermissionResult", "All permissions granted")
                checkLocationAndSetContent()
            } else {
                Log.d("PermissionResult", "Some permissions denied")
                Toast.makeText(
                    this,
                    "Permissions are required for the app to function",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun hasAllPermissions(): Boolean {
        val basePermissionsGranted = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return basePermissionsGranted && REQUIRED_PERMISSIONS_API_29.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        }
        return basePermissionsGranted
    }
}

class HomeViewModelFactory(
    private val repository: NetworkRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}