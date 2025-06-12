package com.netwatcher.polaris.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun addNetworkData(networkData: NetworkData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNetworkData(networkData=networkData)
        }
    }
    fun getAllNetworkData(): Flow<List<NetworkData>> {
        return repository.getAllNetworkData()
    }
    suspend fun getNetworkDataById(id: Long): Flow<NetworkData> {
        return repository.getNetworkDataById(id)
    }
    fun deleteNetworkData(networkData: NetworkData) {
        viewModelScope.launch {
            repository.deleteNetworkData(networkData)
        }
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

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            try {
                val lastResult = repository.getAllNetworkData().firstOrNull()?.lastOrNull()
                _uiState.value = if (lastResult != null) {
                    HomeUiState.Success(lastResult)
                } else {
                    HomeUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to load initial data")
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

