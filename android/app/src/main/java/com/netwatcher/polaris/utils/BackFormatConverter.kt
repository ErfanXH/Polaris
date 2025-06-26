package com.netwatcher.polaris.utils

import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.utils.TimeStampConverter

fun BackFormatConverter(data: NetworkData): Map<String, Any?> {
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