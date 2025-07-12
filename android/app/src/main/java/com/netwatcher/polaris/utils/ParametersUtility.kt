package com.netwatcher.polaris.utils

import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.CellSignalStrengthLte
import android.telephony.CellSignalStrengthNr
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.netwatcher.polaris.domain.model.NetworkData

fun getNetworkType(networkType: Int): String {
    return when (networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
        TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
        TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
        TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
        TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
        TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
        TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
        TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
        TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
        TelephonyManager.NETWORK_TYPE_NR -> "NR"
        TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD"
        TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA"
        TelephonyManager.NETWORK_TYPE_IWLAN -> "IWLAN"
        else -> "Others"
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getCellInfo(cell: CellInfo?) : NetworkData? {
    return when (cell) {
        is CellInfoGsm -> getGsmInfo(cell)
//        is CellInfoCdma -> getCdmaInfo(cell)
        is CellInfoWcdma -> getWcdmaInfo(cell)
        is CellInfoLte -> getLteInfo(cell)
//        is CellInfoNr -> getNrInfo(cell)
        else -> null
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun getGsmInfo(cell: CellInfoGsm) : NetworkData {
    return NetworkData(
        0.0, 0.0, "", "GSM",
        null, cell.cellIdentity.lac.toString(),
        cell.cellIdentity.cid.toString(),
        null,
        "",
        cell.cellIdentity.arfcn,
        getFrequency(cell),
        getFrequencyBand(cell),
        null,
        null,
        null,
        null,
        cell.cellSignalStrength.javaClass.getMethod("getRssi").invoke(cell.cellSignalStrength) as? Int,
        null,
        0.0,0.0,0.0,0.0,0.0, 0.0, ""
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun getWcdmaInfo(cell: CellInfoWcdma) : NetworkData {
    var ecN0 = -1
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        ecN0 = cell.cellSignalStrength.ecNo
    }
    return NetworkData(
        0.0, 0.0, "", "WCDMA",
        null, cell.cellIdentity.lac.toString(),
        cell.cellIdentity.cid.toString(),
        null,
        "",
        cell.cellIdentity.uarfcn,
        getFrequency(cell),
        getFrequencyBand(cell),
        null,
        null,
        cell.cellSignalStrength.dbm,
        ecN0,
        null,
        null,
        0.0,0.0,0.0,0.0,0.0, 0.0, ""
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun getLteInfo(cell: CellInfoLte) : NetworkData {
    return NetworkData(
        0.0, 0.0, "", "LTE",
        cell.cellIdentity.tac.toString(), null,
        cell.cellIdentity.ci.toString(),
        null,
        "",
        cell.cellIdentity.earfcn,
        getFrequency(cell),
        getFrequencyBand(cell),
        cell.cellSignalStrength.rsrp,
        cell.cellSignalStrength.rsrq,
        null,
        null,
        null,
        null,
        0.0,0.0,0.0,0.0,0.0, 0.0, ""
    )
}

private fun calculateNrFrequency(nrarfcn: Int): Double {
    // Implement NR frequency calculation based on 3GPP 38.104
    // This is a simplified example - actual calculation depends on band
    return when {
        nrarfcn in 0..599999 -> 0.001 * nrarfcn + 0.0  // Example for low bands
        else -> 0.0005 * nrarfcn + 0.0  // Example for high bands
    }
}

private fun getNrBand(nrarfcn: Int): String {
    // Map NRARFCN to 5G bands based on 3GPP specifications
    return when (nrarfcn) {
        in 0..599999 -> "n1"  // 2100 MHz
        in 600000..1199999 -> "n3"  // 1800 MHz
        in 1200000..1799999 -> "n7"  // 2600 MHz
        in 1800000..2399999 -> "n28"  // 700 MHz
        in 2400000..2999999 -> "n78"  // 3500 MHz (common 5G band)
        else -> "Unknown NR Band"
    }
}

fun getTac(cellInfo: CellInfo?): String? {
    return when {
        cellInfo is CellInfoLte -> cellInfo.cellIdentity.tac.toString()
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> {
            try {
                val method = cellInfo.cellIdentity.javaClass.getMethod("getTac")
                (method.invoke(cellInfo.cellIdentity) as? Int)?.toString()
            } catch (e: Exception) {
                null
            }
        }
        else -> null
    }
}

fun getLac(cellInfo: CellInfo?): String? {
    return when (cellInfo) {
        is CellInfoGsm -> {
            val lac = cellInfo.cellIdentity.lac
            if (lac != Int.MAX_VALUE) lac.toString() else null
        }

        is CellInfoWcdma -> {
            val lac = cellInfo.cellIdentity.lac
            if (lac != Int.MAX_VALUE) lac.toString() else null
        }

        else -> null
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getCellId(cellInfo: CellInfo?): String? {
    return when (cellInfo) {
        is CellInfoGsm -> cellInfo.cellIdentity.cid.takeIf { it != Int.MAX_VALUE }?.toString()
        is CellInfoLte -> cellInfo.cellIdentity.ci.takeIf { it != Int.MAX_VALUE }?.toString()
        is CellInfoWcdma -> cellInfo.cellIdentity.cid.takeIf { it != Int.MAX_VALUE }?.toString()
        is CellInfoNr -> handlePossibleNrCell(cellInfo)
        else -> null
    }
}

fun handlePossibleNrCell(cellInfo: CellInfo?): String? {
    return if (Build.VERSION.SDK_INT >= 29) {
        try {
            val nrCellInfoClass = Class.forName("android.telephony.CellInfoNr")
            if (nrCellInfoClass.isInstance(cellInfo)) {
                val cellIdentity = nrCellInfoClass.getMethod("getCellIdentity").invoke(cellInfo)
                val nci = cellIdentity?.javaClass?.getMethod("getNci")?.invoke(cellIdentity)
                nci?.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}

fun getRsrp(cellInfo: CellInfo?): Int? {
    return when {
        cellInfo is CellInfoLte -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (cellInfo.cellSignalStrength as? CellSignalStrengthLte)?.getRsrp()
            } else null
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> {
            try {
                val method = CellSignalStrengthNr::class.java.getMethod("getRsrp")
                method.invoke(cellInfo.cellSignalStrength) as? Int
            } catch (e: Exception) {
                null
            }
        }

        else -> null
    }
}

fun getRsrq(cellInfo: CellInfo?): Int? {
    return when {
        cellInfo is CellInfoLte -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (cellInfo.cellSignalStrength as? CellSignalStrengthLte)?.getRsrq()
            } else null
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> {
            try {
                val method = CellSignalStrengthNr::class.java.getMethod("getRsrq")
                method.invoke(cellInfo.cellSignalStrength) as? Int
            } catch (e: Exception) {
                null
            }
        }

        else -> null
    }
}

fun getRscp(cellInfo: CellInfo?): Int? {
    return when (cellInfo) {
        is CellInfoWcdma -> {
            try {
                val method = cellInfo.cellSignalStrength.javaClass.getMethod("getRscp")
                method.invoke(cellInfo.cellSignalStrength) as? Int
            } catch (e: Exception) {
                null
            }
        }

        else -> null
    }
}

fun getRxLev(cellInfo: CellInfo?): Int? {
    return when (cellInfo) {
        is CellInfoGsm -> {
            try {
                val method = cellInfo.cellSignalStrength.javaClass.getMethod("getRssi")
                method.invoke(cellInfo.cellSignalStrength) as? Int
            } catch (e: Exception) {
                null
            }
        }

        else -> null
    }
}

fun getSsRsrp(cellInfo: CellInfo?): Int? {
    return when {
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (cellInfo is CellInfoNr) -> (cellInfo.cellSignalStrength as? CellSignalStrengthNr)?.ssRsrp
        else -> null
    }
}

fun getArfcn(cellInfo: CellInfo?): Int? {
    return when {
        cellInfo is CellInfoGsm -> cellInfo.cellIdentity.arfcn
        cellInfo is CellInfoLte -> cellInfo.cellIdentity.earfcn
        cellInfo is CellInfoWcdma -> cellInfo.cellIdentity.uarfcn

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> {
            try {
                val method = cellInfo.cellIdentity.javaClass.getMethod("getNrarfcn")
                method.invoke(cellInfo.cellIdentity) as? Int
            } catch (e: Exception) {
                null
            }
        }

        else -> null
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getFrequency(cellInfo: CellInfo?): Double? {
    val arfcn = getArfcn(cellInfo) ?: return null
    return when (cellInfo) {
        is CellInfoGsm -> if (arfcn in 0..124) 935.0 + 0.2 * arfcn else 1805.0 + 0.2 * (arfcn - 512)
        is CellInfoLte -> 2110.0 + 0.1 * (arfcn - 0)
        is CellInfoWcdma -> 2110.0 + 0.2 * (arfcn - 10562)
        is CellInfoNr -> 0.001 * arfcn
        else -> null
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getFrequencyBand(cellInfo: CellInfo?): String? {
    val arfcn = getArfcn(cellInfo) ?: return null
    return when (cellInfo) {
        is CellInfoGsm -> when {
            arfcn in 0..124 -> "GSM 900"
            arfcn in 975..1023 -> "GSM 900 (Extended)"
            arfcn in 128..251 -> "GSM 850"
            arfcn in 512..885 -> "GSM 1800"
            else -> "GSM Unknown"
        }
        is CellInfoWcdma -> "WCDMA Band"
        is CellInfoLte -> "LTE Band"
        is CellInfoNr -> "NR Band"
        else -> null
    }
}
