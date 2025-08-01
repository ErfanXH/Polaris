package com.netwatcher.polaris.domain.usecase.home

import android.app.Application
import com.netwatcher.polaris.data.local.CookieManager
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.TestConfigManager
import javax.inject.Inject

class SelectSimUseCase @Inject constructor(
    private val app: Application
) {
    operator fun invoke(simSlotId: Int, simSubsId: Int) {
        TestConfigManager.setSelectedSimSlotId(app, simSlotId)
        TestConfigManager.setSelectedSimSubsId(app, simSubsId)
    }

    fun getSimSlotId(): Int? = TestConfigManager.getSelectedSimSlotId(app)

    fun getSimSubsId(): Int? = TestConfigManager.getSelectedSimSubsId(app)
}

class RunNetworkTestUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend operator fun invoke(simSlotId: Int, simSubsId: Int, selection: TestSelection) =
        repository.runNetworkTest(simSlotId, simSubsId, selection)
}

class LoadInitialStateUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke() = repository.getAllNetworkData()
}

class LogoutUseCase @Inject constructor(
    private val cookieManager: CookieManager
) {
    suspend operator fun invoke(): Boolean {
        return try {
            cookieManager.clearAll()
            true
        } catch (e: Exception) {
            false
        }
    }
}