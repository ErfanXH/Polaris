package com.netwatcher.polaris.domain.usecase.settings

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import com.netwatcher.polaris.domain.model.SimInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LoadSimCardsUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @SuppressLint("MissingPermission")
    operator fun invoke(): List<SimInfo> {
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

        return subscriptionManager.activeSubscriptionInfoList?.map {
            SimInfo(
                displayName = it.displayName?.toString() ?: "Unknown",
                carrierName = it.carrierName?.toString() ?: "Unknown",
                simSlotIndex = it.simSlotIndex,
                subscriptionId = it.subscriptionId
            )
        } ?: emptyList()
    }
}
