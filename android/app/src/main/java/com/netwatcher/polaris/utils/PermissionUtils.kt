package com.netwatcher.polaris.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import android.content.Intent
import android.net.Uri

data class AppPermission(
    val name: String,
    val description: String,
    val settingsIntent: Intent? = null
)

fun requiredPermissions(context: Context): List<AppPermission> {
    val packageName = context.packageName

    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }

    val permissions = mutableListOf(
        AppPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            "Precise location for accurate network measurements",
//            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            appSettingsIntent
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        ),
        AppPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            "Approximate location for basic network info",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.READ_PHONE_STATE,
            "Required to read SIM and network information",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.SEND_SMS,
            "Required for SMS delivery tests",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.RECEIVE_SMS,
            "Required to verify SMS delivery",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.READ_SMS,
            "Required to read SMS delivery reports",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.ACCESS_NETWORK_STATE,
            "Required to check network connectivity",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.CHANGE_NETWORK_STATE,
            "Required to modify network settings",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.ACCESS_WIFI_STATE,
            "Required to check WiFi status",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.FOREGROUND_SERVICE,
            "Required for background operations",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            "Required to restart tests after reboot",
            appSettingsIntent
        ),
        AppPermission(
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            "Please disable battery optimizations to allow background execution.",
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        )
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.add(
            AppPermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                "Required for background network monitoring",
//                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                appSettingsIntent
            )
        )
        permissions.add(
            AppPermission(
                Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                "Required for location-based foreground services",
                appSettingsIntent
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions.add(
            AppPermission(
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                "Required for precise test scheduling",
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(
            AppPermission(
                Manifest.permission.POST_NOTIFICATIONS,
                "Required to show test notifications",
                appSettingsIntent
            )
        )
    }

    return permissions
}

fun permissionStatus(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}