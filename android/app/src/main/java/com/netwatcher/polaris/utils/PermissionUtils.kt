package com.netwatcher.polaris.utils

import android.Manifest
import android.app.AlarmManager
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
    val guide: String,
    val settingsIntent: Intent? = null,
    val permissionString: String? = null
)

fun requiredPermissions(context: Context): List<AppPermission> {
    val packageName = context.packageName

    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }

    val scheduleExactAlarmIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        data = Uri.parse("package:$packageName")
    }

    val ignoreBatteryOptimizationIntent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = Uri.parse("package:$packageName")
    }

    val permissions = mutableListOf(
        AppPermission(
            "Location",
            "Required for accurate network measurements.",
            "Permissions > Location > Turn on the 'Use precise location' and Select 'Allow all the time'",
            appSettingsIntent,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        AppPermission(
            "Phone State",
            "Required to read SIM card information, network type, and signal strength.",
            "Permissions > Phone > Select 'Allow'",
            appSettingsIntent,
            Manifest.permission.READ_PHONE_STATE
        ),
        AppPermission(
            "Receive SMS",
            "Required to receive and verify the SMS delivery tests.",
            "Permissions > SMS > Select 'Allow'",
            appSettingsIntent,
            Manifest.permission.RECEIVE_SMS
        ),
        AppPermission(
            "Send SMS",
            "Required to send SMS delivery tests.",
            "Permissions > SMS > Select 'Allow'",
            appSettingsIntent,
            Manifest.permission.SEND_SMS
        ),
        AppPermission(
            "Read SMS",
            "Required to read SMS delivery reports.",
            "Permissions > SMS > Select 'Allow'",
            appSettingsIntent,
            Manifest.permission.READ_SMS
        ),
        AppPermission(
            "Unrestricted Battery Usage",
            "Prevents system from restricting background operations.",
            "Battery > Battery Optimization > Select 'Don't optimize'",
            ignoreBatteryOptimizationIntent,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        )
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.add(
            AppPermission(
                "Background Location",
                "Required for background network monitoring.",
                "Permissions > Location > Turn on the 'Use precise location' and Select 'Allow all the time'",
                appSettingsIntent,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions.add(
            AppPermission(
                "Schedule Exact Alarm",
                "Required for precise timing of network tests.",
                "Special Permissions > Alarms & Reminders > Enable 'Allow exact alarms'",
//                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM),
                scheduleExactAlarmIntent,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(
            AppPermission(
                "Notifications",
                "Required to show test notifications.",
                "Notifications > Select 'Notifications' > Select 'Allow sound and vibration'",
                appSettingsIntent,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )
    }

    return permissions
}

fun permissionStatus(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.isBatteryOptimizationDisabled(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
    return powerManager.isIgnoringBatteryOptimizations(packageName)
}

fun Context.isScheduleExactAlarmEnabled(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }
    return true
}