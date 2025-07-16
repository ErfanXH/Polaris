package com.netwatcher.polaris.domain.model

sealed class PermissionState {
    data object Granted : PermissionState()
    data object Denied : PermissionState()
    data object PermanentlyDenied : PermissionState()
}