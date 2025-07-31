package com.netwatcher.polaris.presentation.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.data.local.CookieManager
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.domain.usecase.home.HomeUseCases
import com.netwatcher.polaris.utils.TestAlarmScheduler
import com.netwatcher.polaris.utils.TestConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases,
    private val app: Application
) : ViewModel() {
    val context = app
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var selectedSimSlotId: Int? by mutableStateOf(
        homeUseCases.selectedSim.getSimSlotId()
    )

    private var selectedSimSubsId: Int? by mutableStateOf(
        homeUseCases.selectedSim.getSimSubsId()
    )

    fun loadSelectedSim() {
        selectedSimSlotId = homeUseCases.selectedSim.getSimSlotId()
        selectedSimSubsId = homeUseCases.selectedSim.getSimSubsId()
    }

    fun runNetworkTest(testSelection: TestSelection) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = homeUseCases.runNetworkTest(
                    selectedSimSlotId ?: 0,
                    selectedSimSubsId ?: 0,
                    testSelection
                )
                if (result.isValid()) {
                    _uiState.value = HomeUiState.Success(result)
                } else {
                    Toast.makeText(app, "Network Test Failed!", Toast.LENGTH_SHORT).show()
                    loadInitialState()
                }

                TestAlarmScheduler.rescheduleTest(app)
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
                val lastResult = homeUseCases.loadInitialState().firstOrNull()?.lastOrNull()
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
        return homeUseCases.logout()
    }
}
