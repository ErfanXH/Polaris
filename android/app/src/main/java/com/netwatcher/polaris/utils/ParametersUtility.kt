package com.netwatcher.polaris.utils

import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.CellSignalStrengthLte
import android.telephony.CellSignalStrengthNr
import androidx.annotation.RequiresApi

fun getNetworkType(cellInfo: CellInfo?): String {
    return when {
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (cellInfo is CellInfoNr) -> "5G"
        cellInfo is CellInfoLte -> "LTE"
        cellInfo is CellInfoWcdma -> "HSPA"
        cellInfo is CellInfoGsm -> "GSM"
        else -> "UNKNOWN"
    }
}

//fun getTac(cellInfo: CellInfo?): String? {
//    val tac = when {
//        cellInfo is CellInfoLte -> cellInfo.cellIdentity.tac.toString()
//        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> "N/A"
//        else -> null
//    }
//    return tac
//}
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

@RequiresApi(Build.VERSION_CODES.R)
@Suppress("DiscouragedApi")
fun getRac(cellInfo: CellInfo?): String? {
    return when (cellInfo) {
        is CellInfoGsm, is CellInfoWcdma -> {
            try {
                val racField = cellInfo.cellIdentity.javaClass.getDeclaredField("mRac")
                racField.isAccessible = true
                (racField.get(cellInfo.cellIdentity) as? Int)?.takeIf { it != Int.MAX_VALUE }?.toString()
            } catch (e: Exception) {
                null
            }
        }
        else -> null
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

fun getEcIo(cellInfo: CellInfo?): Int? {
    return when (cellInfo) {
        is CellInfoWcdma -> {
            try {
                val method = cellInfo.cellSignalStrength.javaClass.getMethod("getEcNo")
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
        is CellInfoLte -> "LTE Band (EARFCN: $arfcn)"
        is CellInfoWcdma -> "WCDMA Band (UARFCN: $arfcn)"
        is CellInfoNr -> "NR Band (NRARFCN: $arfcn)"
        else -> null
    }
}
