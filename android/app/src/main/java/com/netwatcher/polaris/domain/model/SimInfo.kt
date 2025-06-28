package com.netwatcher.polaris.domain.model

data class SimInfo(
    val displayName: String,
    val carrierName: String,
    val simSlotIndex: Int,
    val subscriptionId: Int
)