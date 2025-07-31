package com.netwatcher.polaris.domain.usecase.permission

import android.content.Context
import com.netwatcher.polaris.presentation.permission.PermissionItemState
import com.netwatcher.polaris.utils.isBatteryOptimizationDisabled
import com.netwatcher.polaris.utils.permissionStatus
import com.netwatcher.polaris.utils.requiredPermissions
import javax.inject.Inject

class PermissionUseCase @Inject constructor() {
    operator fun invoke(context: Context): List<PermissionItemState> {
        return requiredPermissions(context).map { permission ->
            val isGranted = when (permission.permissionString) {
                "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" -> context.isBatteryOptimizationDisabled()
                else -> permission.permissionString?.let { permissionStatus(context, it) } ?: true
            }
            PermissionItemState(permission = permission, isGranted = isGranted)
        }
    }
}