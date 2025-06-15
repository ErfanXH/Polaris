package com.netwatcher.polaris.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.TimeStampConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val repository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private fun convertToBackFormat(data: NetworkData): Map<String, Any?> {
        return mapOf(
            "latitude" to data.latitude,
            "longitude" to data.longitude,
            "timestamp" to TimeStampConverter(data.timestamp),
            "network_type" to data.networkType,
            "tac" to data.tac,
            "lac" to data.lac,
            "cell_id" to data.cellId,
            "rac" to data.rac,
            "plmn_id" to data.plmnId,
            "arfcn" to data.arfcn,
            "frequency" to data.frequency,
            "frequency_band" to data.frequencyBand,
            "rsrp" to data.rsrp,
            "rsrq" to data.rsrq,
            "rscp" to data.rscp,
            "ecIo" to data.ecIo,
            "rxLev" to data.rxLev,
            "ssRsrp" to data.ssRsrp,
            "http_upload" to data.httpUploadThroughput,
            "http_download" to data.httpDownloadThroughput,
            "ping_time" to data.pingTime,
            "dns_response" to data.dnsResponse,
            "web_response" to data.webResponse,
            "sms_delivery_time" to data.smsDeliveryTime
        )
    }

    fun runNetworkTest() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val result = repository.runNetworkTest()
                Log.d("HomeViewModel", "Test result: $result")
                _uiState.value = HomeUiState.Success(result)
                repository.uploadNetworkData(convertToBackFormat(result))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error: ${e.message}", e)
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error occurred")
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
                _uiState.value = HomeUiState.Error("Failed to load initial data")
                Log.d("userInfo","failed to load user")
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

