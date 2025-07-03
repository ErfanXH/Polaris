package com.netwatcher.polaris.presentation.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.presentation.home.HomeUiState
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.TestAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel for the HomeScreen.
 * Inherits from AndroidViewModel to get access to the application context,
 * which is needed for rescheduling alarms.
 */
class HomeViewModel(
    private val repository: NetworkRepository,
    application: Application // Add application parameter
) : AndroidViewModel(application) { // Inherit from AndroidViewModel

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var selectedSubscriptionId: Int? = null

    fun setSelectedSim(subscriptionId: Int) {
        selectedSubscriptionId = subscriptionId
    }

    /**
     * Runs a manual network test, saves the result locally, and reschedules the next background test.
     */
    fun runNetworkTest() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = repository.runNetworkTest(selectedSubscriptionId)
                _uiState.value = HomeUiState.Success(result)

                // Reschedule the background test alarm after a manual run
                TestAlarmScheduler.rescheduleTest(getApplication())
                Log.d("HomeViewModel", "Manual test run. Background test rescheduled.")

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Network Test Failed")
            }
        }
    }

    init {
        loadInitialState()
    }

    /**
     * Loads the last test result from the local database to show on the screen.
     */
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
