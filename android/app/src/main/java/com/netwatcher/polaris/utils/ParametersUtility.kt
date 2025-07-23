package com.netwatcher.polaris.utils

import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.CellSignalStrengthLte
import android.telephony.CellSignalStrengthNr
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.netwatcher.polaris.domain.model.NetworkData

data class GsmBand(val band: String, val arfcnStart: Int, val arfcnEnd: Int, val dlFreqStartMHz: Double)
data class WcdmaBand(val band: String, val uarfcnStart: Int, val uarfcnEnd: Int, val freqStartMHz: Double)
data class LteBand(val band: String, val dlEarfcnStart: Int, val dlEarfcnEnd: Int, val dlFreqStartMHz: Double)

private val gsmBands = listOf(
    GsmBand("GSM 900", 1, 124, 935.2),
    GsmBand("Extended GSM 900", 975, 1023, 925.2),
    GsmBand("GSM 1800", 512, 885, 1805.2)
)

private val wcdmaBands = listOf(
    WcdmaBand("1", 10562, 10838, 2112.4),
    WcdmaBand("2", 9662, 9938, 1932.4),
    WcdmaBand("3", 1162, 1513, 1807.4),
    WcdmaBand("4", 1537, 1738, 2112.4),
    WcdmaBand("5", 4357, 4458, 877.4),
    WcdmaBand("8", 2937, 3088, 925.4)
)

private val lteBands = listOf(
    LteBand("1", 0, 599, 2110.0),
    LteBand("3", 1200, 1949, 1805.0),
    LteBand("7", 2750, 3449, 2620.0),
    LteBand("8", 3450, 3799, 925.0),
    LteBand("20", 6150, 6449, 791.0),
    LteBand("28", 9210, 9659, 758.0),
    LteBand("38", 37750, 38249, 2570.0),
    LteBand("40", 38650, 39649, 2300.0),
    LteBand("41", 39650, 41589, 2496.0)
)

fun getCellInfo(cell: CellInfo?, networkType: String) : NetworkData? {
    return when (cell) {
        is CellInfoGsm -> getGsmInfo(cell, networkType)
        is CellInfoWcdma -> getWcdmaInfo(cell, networkType)
        is CellInfoLte -> getLteInfo(cell, networkType)
        else -> null
    }
}

private fun getGsmInfo(cell: CellInfoGsm, networkType: String) : NetworkData {
    val arfcn = cell.cellIdentity.arfcn
    val lac = if (cell.cellIdentity.lac != Int.MAX_VALUE) cell.cellIdentity.lac.toString() else null
    val cellId = if (cell.cellIdentity.cid != Int.MAX_VALUE) cell.cellIdentity.cid.toString() else null
    val net = if (networkType != "GPRS" && networkType != "EDGE" && networkType != "CDMA") "GSM" else networkType
    val rssi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) cell.cellSignalStrength.javaClass.getMethod("getRssi").invoke(cell.cellSignalStrength) as? Int else null
    return NetworkData(
        0.0, 0.0, "", net, null, lac, cellId, "",
        arfcn, getGsmFrequency(arfcn), getGsmFrequencyBand(arfcn),
        null, null, null, null, rssi,
        0.0,0.0,0.0,0.0,0.0, 0.0, ""
    )
}

private fun getWcdmaInfo(cell: CellInfoWcdma, networkType: String) : NetworkData {
    val uarfcn = cell.cellIdentity.uarfcn
    var ecN0 = -1
    val lac = if (cell.cellIdentity.lac != Int.MAX_VALUE) cell.cellIdentity.lac.toString() else null
    val cellId = if (cell.cellIdentity.cid != Int.MAX_VALUE) cell.cellIdentity.cid.toString() else null
    val net = if (networkType != "HSDPA" && networkType != "HSUPA" && networkType != "UMTS" && networkType != "HSPA" && networkType != "HSPA+") "3G" else networkType
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ecN0 = cell.cellSignalStrength.ecNo
    }
    return NetworkData(
        0.0, 0.0, "", net, null, lac, cellId, "",
        uarfcn, getWcdmaFrequency(uarfcn), "WCDMA Band ${getWcdmaFrequencyBand(uarfcn)}",
        null, null, cell.cellSignalStrength.dbm, ecN0, null,
        0.0,0.0,0.0,0.0,0.0, 0.0, ""
    )
}

private fun getLteInfo(cell: CellInfoLte, networkType: String) : NetworkData {
    val earfcn = cell.cellIdentity.earfcn
    val cellId = if (cell.cellIdentity.ci != Int.MAX_VALUE) cell.cellIdentity.ci.toString() else null
    return NetworkData(
        0.0, 0.0, "", "LTE",
        cell.cellIdentity.tac.toString(), null, cellId, "",
        earfcn, getLTEFrequency(earfcn), "LTE Band ${getLTEFrequencyBand(earfcn)}",
        cell.cellSignalStrength.rsrp, cell.cellSignalStrength.rsrq,
        null, null, null, 0.0,0.0,0.0,
        0.0,0.0, 0.0, ""
    )
}

fun getGsmFrequency(arfcn: Int): Double? {
    val band = gsmBands.find { arfcn in it.arfcnStart..it.arfcnEnd } ?: return null
    return band.dlFreqStartMHz + 0.2 * (arfcn - band.arfcnStart)
}

fun getGsmFrequencyBand(arfcn: Int): String? {
    return gsmBands.find { arfcn in it.arfcnStart..it.arfcnEnd }?.band
}

fun getWcdmaFrequency(uarfcn: Int): Double? {
    val band = wcdmaBands.find { uarfcn in it.uarfcnStart..it.uarfcnEnd } ?: return null
    return band.freqStartMHz + 0.2 * (uarfcn - band.uarfcnStart)
}

fun getWcdmaFrequencyBand(uarfcn: Int): String? {
    return wcdmaBands.find { uarfcn in it.uarfcnStart..it.uarfcnEnd }?.band
}

fun getLTEFrequency(earfcn: Int): Double? {
    val band = lteBands.find { earfcn in it.dlEarfcnStart..it.dlEarfcnEnd } ?: return null
    return band.dlFreqStartMHz + 0.1 * (earfcn - band.dlEarfcnStart)
}

fun getLTEFrequencyBand(earfcn: Int): String? {
    return lteBands.find { earfcn in it.dlEarfcnStart..it.dlEarfcnEnd }?.band
}