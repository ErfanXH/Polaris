package com.netwatcher.polaris.domain.model

data class Measurement(
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: String,
    val network_type: String,
    val tac: String?,
    val lac: String?,
    val rac: String?,
    val cell_id: String?,
    val plmn_id: String?,
    val arfcn: Int?,
    val frequency: Double?,
    val frequency_band: String?,
    val rsrp: Int?,
    val rsrq: Int?,
    val rscp: Int?,
    val ecIo: Int?,
    val rxLev: Int?,
    val ssRsrp: Int?,
    val http_upload: Double,
    val http_download: Double,
    val ping_time: Double,
    val dns_response: Int,
    val web_response: Long?,
    val sms_delivery_time: Int
)

data class MeasurementRequest(
    val measurements: List<Measurement>
)
