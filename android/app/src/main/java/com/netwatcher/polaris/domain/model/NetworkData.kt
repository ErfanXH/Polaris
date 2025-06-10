package com.netwatcher.polaris.domain.model

data class NetworkData (
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val networkType: String,
    val tac: String?,
    val lac: String?,
    val cellId: String?,
    val rac: String?,
    val plmnId: String?,
    val arfcn: Int?,
    val frequency: Double?,
    val frequencyBand: String?,
    val rsrp: Int?,
    val rsrq: Int?,
    val rscp: Int?,
    val ecIo: Int?,
    val rxLev: Int?,
    val ssRsrp: Int?,
    val httpThroughput: Double,
    val pingTime: Double,
    val dnsResponse: Int,
    val webResponse: Long?,
    val smsDeliveryTime: Int
) {
}