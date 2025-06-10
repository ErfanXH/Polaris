package eh.learning.homepage.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eh.learning.homepage.data.model.NetworkData
import eh.learning.homepage.data.repository.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.location.Location

class HomeViewModel(
    private val repository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadLastTestResult()
    }

    fun runNetworkTest() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = repository.runNetworkTest()
                Log.d("HomeViewModel", "Test result: $result")
                _uiState.value = HomeUiState.Success(result)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error: ${e.message}", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadLastTestResult() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = repository.getLastTestResult()
                _uiState.value = if (result != null) {
                    HomeUiState.Success(result)
                } else {
                    HomeUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val location = repository.getCurrentLocation()
                if (location != null) {
                    _uiState.value = HomeUiState.LocationSuccess(location)
                } else {
                    _uiState.value = HomeUiState.Error("Unable to get location")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Location error")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val data: NetworkData) : HomeUiState()
    data class LocationSuccess(val location: Location) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}