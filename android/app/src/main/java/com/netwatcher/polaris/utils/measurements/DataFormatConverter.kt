package com.netwatcher.polaris.utils.measurements

import com.netwatcher.polaris.domain.model.Measurement
import com.netwatcher.polaris.domain.model.NetworkData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun timeStampConverter(timestamp: String): String {
    val inputFormat = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
    val dateTime = LocalDateTime.parse(timestamp, inputFormat)
    return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z"
}

fun measurementConverter(networkData: NetworkData): Measurement {
    return Measurement(
        latitude = networkData.latitude,
        longitude = networkData.longitude,
        timestamp = timeStampConverter(networkData.timestamp),
        network_type = networkData.networkType,
        tac = networkData.tac,
        lac = networkData.lac,
        cell_id = networkData.cellId,
        plmn_id = networkData.plmnId,
        arfcn = networkData.arfcn,
        frequency = networkData.frequency,
        frequency_band = networkData.frequencyBand,
        rsrp = networkData.rsrp,
        rsrq = networkData.rsrq,
        rscp = networkData.rscp,
        ecIo = networkData.ecIo,
        rxLev = networkData.rxLev,
        http_upload = networkData.httpUploadThroughput,
        http_download = networkData.httpDownloadThroughput,
        ping_time = networkData.pingTime,
        dns_response = networkData.dnsResponse,
        web_response = networkData.webResponse,
        sms_delivery_time = networkData.smsDeliveryTime
    )
}