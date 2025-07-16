package com.netwatcher.polaris.presentation.settings

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.telephony.SubscriptionManager
import androidx.lifecycle.AndroidViewModel
import com.netwatcher.polaris.domain.model.SimInfo
import com.netwatcher.polaris.utils.DataSyncScheduler
import com.netwatcher.polaris.utils.TestConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _simList = MutableStateFlow<List<SimInfo>>(emptyList())
    val simList = _simList.asStateFlow()

    private val _selectedSimSlotId = MutableStateFlow(TestConfigManager.getSelectedSimSlotId(application))
    val selectedSimSlotId = _selectedSimSlotId.asStateFlow()

    private val _selectedSimSubsId = MutableStateFlow(TestConfigManager.getSelectedSimSlotId(application))
    val selectedSimSubsId = _selectedSimSubsId.asStateFlow()

    private val _selectedInterval = MutableStateFlow(
        DataSyncScheduler.getPreferences(application).getLong(DataSyncScheduler.KEY_SYNC_INTERVAL, 30L)
    )
    val selectedInterval = _selectedInterval.asStateFlow()

    init {
        loadSimCards()
    }

    @SuppressLint("MissingPermission")
    private fun loadSimCards() {
        val application = getApplication<Application>()
        val subscriptionManager =
            application.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        val list = subscriptionManager.activeSubscriptionInfoList?.map {
            SimInfo(
                displayName = it.displayName?.toString() ?: "Unknown",
                carrierName = it.carrierName?.toString() ?: "Unknown",
                simSlotIndex = it.simSlotIndex,
                subscriptionId = it.subscriptionId
            )
        } ?: emptyList()

        _simList.value = list

        // If no sim was saved (or invalid), default to the first sim
        val savedSimSlotId = TestConfigManager.getSelectedSimSlotId(application)
        val validSaved = list.any { it.simSlotIndex == savedSimSlotId }
        val savedSimSubsId = TestConfigManager.getSelectedSimSubsId(application)

        val defaultSimSlotId = if (validSaved) savedSimSlotId else list.firstOrNull()?.simSlotIndex
        val defaultSimSubsId = if (validSaved) savedSimSubsId else list.firstOrNull()?.subscriptionId

        if (defaultSimSlotId != null) {
            _selectedSimSlotId.value = defaultSimSlotId
            _selectedSimSubsId.value = defaultSimSubsId
            TestConfigManager.setSelectedSimSlotId(application, defaultSimSlotId)
            TestConfigManager.setSelectedSimSubsId(application, defaultSimSubsId)
        }
    }

    fun selectSim(simSlotId: Int, simSubsId: Int) {
        _selectedSimSlotId.value = simSlotId
        TestConfigManager.setSelectedSimSlotId(getApplication(), simSlotId)
        TestConfigManager.setSelectedSimSubsId(getApplication(), simSubsId)
    }

    fun updateSyncInterval(minutes: Long) {
        _selectedInterval.value = minutes
        DataSyncScheduler.updateSyncInterval(getApplication(), minutes)
    }

    fun setSmsTestNumber(number: String) {
        TestConfigManager.setSmsTestNumber(getApplication(), number)
    }

    fun setPingAddress(address: String) {
        TestConfigManager.setPingTestAddress(getApplication(), address)
    }

    fun setDnsAddress(address: String) {
        TestConfigManager.setDnsTestAddress(getApplication(), address)
    }

    fun setWebAddress(address: String) {
        TestConfigManager.setWebTestAddress(getApplication(), address)
    }
}
