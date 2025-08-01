package com.netwatcher.polaris.domain.usecase.permission

import android.content.Context
import com.netwatcher.polaris.presentation.permission.PermissionItemState
import com.netwatcher.polaris.utils.permission.isBatteryOptimizationDisabled
import com.netwatcher.polaris.utils.permission.isScheduleExactAlarmEnabled
import com.netwatcher.polaris.utils.permission.permissionStatus
import com.netwatcher.polaris.utils.permission.requiredPermissions
import javax.inject.Inject

class PermissionUseCase @Inject constructor() {
    operator fun invoke(context: Context): List<PermissionItemState> {
        return requiredPermissions(context).map { permission ->
            val isGranted = when (permission.permissionString) {
                "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" -> context.isBatteryOptimizationDisabled()
                "android.permission.SCHEDULE_EXACT_ALARM" -> context.isScheduleExactAlarmEnabled()
                else -> permission.permissionString?.let { permissionStatus(context, it) } ?: true
            }
            PermissionItemState(permission = permission, isGranted = isGranted)
        }
    }
}