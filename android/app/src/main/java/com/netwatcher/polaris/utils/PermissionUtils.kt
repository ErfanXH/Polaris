package com.netwatcher.polaris.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

data class AppPermission(
    val name: String,
    val description: String,
    val settingsIntent: String? = null
)

fun requiredPermissions(context: Context): List<AppPermission> {
    val permissions = mutableListOf(
        AppPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            "Precise location for accurate network measurements",
            Settings.ACTION_LOCATION_SOURCE_SETTINGS
        ),
        AppPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            "Approximate location for basic network info",
        ),
        AppPermission(
            Manifest.permission.READ_PHONE_STATE,
            "Required to read SIM and network information",
        ),
        AppPermission(
            Manifest.permission.SEND_SMS,
            "Required for SMS delivery tests",
        ),
        AppPermission(
            Manifest.permission.RECEIVE_SMS,
            "Required to verify SMS delivery",
        ),
        AppPermission(
            Manifest.permission.READ_SMS,
            "Required to read SMS delivery reports",
        ),
        AppPermission(
            Manifest.permission.ACCESS_NETWORK_STATE,
            "Required to check network connectivity",
        ),
        AppPermission(
            Manifest.permission.CHANGE_NETWORK_STATE,
            "Required to modify network settings",
        ),
        AppPermission(
            Manifest.permission.ACCESS_WIFI_STATE,
            "Required to check WiFi status",
        ),
        AppPermission(
            Manifest.permission.FOREGROUND_SERVICE,
            "Required for background operations",
        ),
        AppPermission(
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            "Required to restart tests after reboot",
        )
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.add(
            AppPermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                "Required for background network monitoring",
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
        )
        permissions.add(
            AppPermission(
                Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                "Required for location-based foreground services",
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions.add(
            AppPermission(
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                "Required for precise test scheduling",
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(
            AppPermission(
                Manifest.permission.POST_NOTIFICATIONS,
                "Required to show test notifications",
            )
        )
    }

    return permissions
}

fun permissionStatus(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}