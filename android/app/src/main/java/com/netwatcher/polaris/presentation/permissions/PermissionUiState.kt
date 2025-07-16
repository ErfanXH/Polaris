package com.netwatcher.polaris.presentation.permissions

import com.netwatcher.polaris.domain.model.PermissionState
import com.netwatcher.polaris.utils.AppPermission

sealed class PermissionUiState {
    data object Loading : PermissionUiState()
    data class Success(val permissions: List<PermissionItemState>) : PermissionUiState()
    data class Error(val message: String) : PermissionUiState()
}

data class PermissionItemState(
    val permission: AppPermission,
    val state: PermissionState,
    val shouldShowRationale: Boolean
)