package com.netwatcher.polaris.presentation.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.di.CookieManager
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.TestAlarmScheduler
import com.netwatcher.polaris.utils.TestConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: NetworkRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var selectedSimSlotId: Int? by mutableStateOf(
        TestConfigManager.getSelectedSimSlotId(application)
    )

    private var selectedSimSubsId: Int? by mutableStateOf(
        TestConfigManager.getSelectedSimSubsId(application)
    )

    fun setSelectedSim(simSlotId: Int, simSubsId: Int) {
        selectedSimSlotId = simSlotId
        selectedSimSubsId = simSubsId
        TestConfigManager.setSelectedSimSlotId(getApplication(), simSlotId)
        TestConfigManager.setSelectedSimSubsId(getApplication(), simSubsId)
    }

    fun runNetworkTest(testSelection: TestSelection) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = repository.runNetworkTest(
                    selectedSimSlotId ?: 0,
                    selectedSimSubsId ?: 0,
                    testSelection
                )
                _uiState.value = HomeUiState.Success(result)

                TestAlarmScheduler.rescheduleTest(getApplication())
                Log.d("HomeViewModel", "Manual test run. Background test rescheduled.")
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Network Test Failed!")
            }
        }
    }

    init {
        loadInitialState()
    }

    fun loadInitialState() {
        viewModelScope.launch {
            try {
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

    suspend fun onLogoutClick(): Boolean {
        return try {
            CookieManager.clearAll()
            true
        } catch (e: Exception) {
            false
        }
    }
}
