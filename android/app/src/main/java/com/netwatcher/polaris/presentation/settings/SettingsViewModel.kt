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

    private val _selectedSimId = MutableStateFlow(TestConfigManager.getSelectedSimId(application))
    val selectedSimId = _selectedSimId.asStateFlow()

    private val _selectedInterval = MutableStateFlow(
        DataSyncScheduler.getPreferences(application).getLong(DataSyncScheduler.KEY_SYNC_INTERVAL, 30L)
    )
    val selectedInterval = _selectedInterval.asStateFlow()

    init {
        loadSimCards()
    }

    @SuppressLint("MissingPermission")
    private fun loadSimCards() {
        val subscriptionManager = getApplication<Application>()
            .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        val list = subscriptionManager.activeSubscriptionInfoList?.map {
            SimInfo(
                displayName = it.displayName?.toString() ?: "Unknown",
                carrierName = it.carrierName?.toString() ?: "Unknown",
                simSlotIndex = it.simSlotIndex,
                subscriptionId = it.subscriptionId
            )
        } ?: emptyList()
        _simList.value = list
    }

    fun selectSim(simId: Int) {
        _selectedSimId.value = simId
        TestConfigManager.setSelectedSimId(getApplication(), simId)
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
