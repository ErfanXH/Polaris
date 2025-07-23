package com.netwatcher.polaris.presentation.permissions

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.netwatcher.polaris.utils.permissions.AppPermission
import com.netwatcher.polaris.utils.permissions.isBatteryOptimizationDisabled
import com.netwatcher.polaris.utils.permissions.isScheduleExactAlarmEnabled
import com.netwatcher.polaris.utils.permissions.permissionStatus
import com.netwatcher.polaris.utils.permissions.requiredPermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PermissionItemState(
    val permission: AppPermission,
    val isGranted: Boolean
)

class PermissionsViewModel : ViewModel() {
    private val _permissionStates = MutableStateFlow<List<PermissionItemState>>(emptyList())
    val permissionStates = _permissionStates.asStateFlow()

    private val _showRationaleDialog = mutableStateOf<AppPermission?>(null)
    val showRationaleDialog: AppPermission?
        get() = _showRationaleDialog.value

    fun onShowRationale(permission: AppPermission) {
        _showRationaleDialog.value = permission
    }

    fun onDismissRationale() {
        _showRationaleDialog.value = null
    }

    fun updatePermissionStates(context: Context) {
        _permissionStates.value = requiredPermissions(context).map { permission ->
            val isGranted = when (permission.permissionString) {
                "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" -> context.isBatteryOptimizationDisabled()
                "android.permission.SCHEDULE_EXACT_ALARM" -> context.isScheduleExactAlarmEnabled()
                else -> permission.permissionString?.let { permissionStatus(context, it) } ?: true
            }
            PermissionItemState(permission = permission, isGranted = isGranted)
        }
    }
}