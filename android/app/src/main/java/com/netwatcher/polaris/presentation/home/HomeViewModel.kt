package com.netwatcher.polaris.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.BackFormatConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var selectedSubscriptionId: Int? = null

    fun setSelectedSim(subscriptionId: Int) {
        selectedSubscriptionId = subscriptionId
    }

    fun runNetworkTest() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = repository.runNetworkTest()
                _uiState.value = HomeUiState.Success(result)
                repository.uploadNetworkData(BackFormatConverter(result))
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Network Test Failed")
            }
        }
    }

    init {
        loadInitialState()
    }

    fun loadInitialState() {
        viewModelScope.launch {
            try {
                val r = repository.getUserInfo()
                Log.d("userInfo", r.isSuccess.toString())
                val lastResult = repository.getAllNetworkData().firstOrNull()?.lastOrNull()
                _uiState.value = if (lastResult != null) {
                    HomeUiState.Success(lastResult)
                } else {
                    HomeUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed To Load Previous Data")
            }
        }
    }
}