package com.netwatcher.polaris.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "networkData-table")
data class NetworkData (
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val networkType: String?,
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
    val httpUploadThroughput: Double?,
    val httpDownloadThroughput: Double?,
    val pingTime: Double?,
    val dnsResponse: Double?,
    val webResponse: Double?,
    val smsDeliveryTime: Double?,
    val email: String?,
    @ColumnInfo(defaultValue = "0")
    val isSynced: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
) {
    companion object {
        fun empty() = NetworkData(
            latitude = -1.0,
            longitude = -1.0,
            timestamp = SimpleDateFormat("HH:mm:ss dd-MM-yyyy",  Locale.getDefault()).format(Date()),
            networkType = "OTHERS",
            tac = null,
            lac = null,
            cellId = null,
            rac = null,
            plmnId = null,
            arfcn = null,
            frequency = null,
            frequencyBand = null,
            rsrp = null,
            rsrq = null,
            rscp = null,
            ecIo = null,
            rxLev = null,
            ssRsrp = null,
            httpUploadThroughput = -1.0,
            httpDownloadThroughput = -1.0,
            pingTime = -1.0,
            dnsResponse = -1.0,
            webResponse = -1.0,
            smsDeliveryTime = -1.0,
            email = "",
            isSynced = false
        )

        fun invalid() = empty().copy(
            networkType = "OTHERS",
            timestamp = "INVALID"
        )
    }

    fun isValid(): Boolean {
        return !(latitude == -1.0 || longitude == -1.0 || networkType == "OTHERS")
    }
}