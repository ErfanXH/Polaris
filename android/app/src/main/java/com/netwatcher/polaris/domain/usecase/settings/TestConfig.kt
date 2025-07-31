package com.netwatcher.polaris.domain.usecase.settings

import android.content.Context
import com.netwatcher.polaris.utils.TestConfigManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class TestConfigUseCases @Inject constructor(
    val setSmsTestNumber: SetSmsTestNumberUseCase,
    val setPingAddress: SetPingAddressUseCase,
    val setDnsAddress: SetDnsAddressUseCase,
    val setWebAddress: SetWebAddressUseCase,
    val setSelectedSim: SetSelectedSimUseCase,
    val getSelectedSimSlotId: GetSelectedSimSlotIdUseCase,
    val getSelectedSimSubsId: GetSelectedSimSubsIdUseCase,
)

class SetSmsTestNumberUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(number: String) {
        TestConfigManager.setSmsTestNumber(context, number)
    }
}

class SetPingAddressUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(address: String) {
        TestConfigManager.setPingTestAddress(context, address)
    }
}

class SetDnsAddressUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(address: String) {
        TestConfigManager.setDnsTestAddress(context, address)
    }
}

class SetWebAddressUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(address: String) {
        TestConfigManager.setWebTestAddress(context, address)
    }
}

class SetSelectedSimUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(simSlotId: Int?, simSubsId: Int?) {
        TestConfigManager.setSelectedSimSlotId(context, simSlotId)
        TestConfigManager.setSelectedSimSubsId(context, simSubsId)
    }
}

class GetSelectedSimSlotIdUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Int? = TestConfigManager.getSelectedSimSlotId(context)
}

class GetSelectedSimSubsIdUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Int? = TestConfigManager.getSelectedSimSubsId(context)
}
