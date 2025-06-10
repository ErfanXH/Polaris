package com.netwatcher.polaris.presentation.home

import android.location.Location
import com.netwatcher.polaris.domain.model.NetworkData

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val data: NetworkData) : HomeUiState()
    data class LocationSuccess(val location: Location) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}