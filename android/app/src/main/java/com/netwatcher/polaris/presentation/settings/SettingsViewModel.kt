package com.netwatcher.polaris.presentation.settings

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.telephony.SubscriptionManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.netwatcher.polaris.domain.model.SimInfo
import com.netwatcher.polaris.domain.usecase.settings.SettingsUseCases
import com.netwatcher.polaris.presentation.home.HomeViewModel
import com.netwatcher.polaris.utils.DataSyncScheduler
import com.netwatcher.polaris.utils.TestConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {

    private val _simList = MutableStateFlow<List<SimInfo>>(emptyList())
    val simList = _simList.asStateFlow()

    private val _selectedSimSlotId =
        MutableStateFlow(settingsUseCases.testConfig.getSelectedSimSlotId())
    val selectedSimSlotId = _selectedSimSlotId.asStateFlow()

    private val _selectedSimSubsId =
        MutableStateFlow(settingsUseCases.testConfig.getSelectedSimSlotId())
    val selectedSimSubsId = _selectedSimSubsId.asStateFlow()

    private val _selectedInterval = MutableStateFlow(
        settingsUseCases.updateSyncInterval.getCurrentInterval()
    )
    val selectedInterval = _selectedInterval.asStateFlow()

    init {
        loadSimCards()
    }

    @SuppressLint("MissingPermission")
    private fun loadSimCards() {
        val list = settingsUseCases.loadSimCards()

        _simList.value = settingsUseCases.loadSimCards()

        // If no sim was saved (or invalid), default to the first sim
        val savedSimSlotId = TestConfigManager.getSelectedSimSlotId(context)
        val validSaved = list.any { it.simSlotIndex == savedSimSlotId }
        val savedSimSubsId = TestConfigManager.getSelectedSimSubsId(context)

        val defaultSimSlotId = if (validSaved) savedSimSlotId else list.firstOrNull()?.simSlotIndex
        val defaultSimSubsId =
            if (validSaved) savedSimSubsId else list.firstOrNull()?.subscriptionId

        if (defaultSimSlotId != null) {
            _selectedSimSlotId.value = defaultSimSlotId
            _selectedSimSubsId.value = defaultSimSubsId
            settingsUseCases.testConfig.setSelectedSim(defaultSimSlotId, defaultSimSubsId)
        }
    }

    fun selectSim(simSlotId: Int, simSubsId: Int) {
        _selectedSimSlotId.value = simSlotId
        _selectedSimSubsId.value = simSubsId
        settingsUseCases.testConfig.setSelectedSim(simSlotId, simSubsId)
    }

    fun updateSyncInterval(minutes: Long) {
        _selectedInterval.value = minutes
        settingsUseCases.updateSyncInterval.update(minutes)
    }

    fun setSmsTestNumber(number: String) {
        settingsUseCases.testConfig.setSmsTestNumber(number)
    }

    fun setPingAddress(address: String) {
        settingsUseCases.testConfig.setPingAddress(address)
    }

    fun setDnsAddress(address: String) {
        settingsUseCases.testConfig.setDnsAddress(address)
    }

    fun setWebAddress(address: String) {
        settingsUseCases.testConfig.setWebAddress(address)
    }

}
