package com.netwatcher.polaris.presentation.permission

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.netwatcher.polaris.domain.usecase.permission.PermissionUseCase
import com.netwatcher.polaris.utils.permission.AppPermission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val permissionUseCase: PermissionUseCase,
    private val app: Application
) : ViewModel() {
    val context = app
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

    fun updatePermissionStates() {
        _permissionStates.value = permissionUseCase(app)
    }
}

data class PermissionItemState(
    val permission: AppPermission,
    val isGranted: Boolean
)