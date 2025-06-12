package com.netwatcher.polaris.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Looper
import android.telephony.*
import android.telephony.TelephonyManager.*
import android.util.Log
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.NetworkDataDao
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.DnsUtility
import com.netwatcher.polaris.utils.LocationUtility
import com.netwatcher.polaris.utils.PingUtility
import com.netwatcher.polaris.utils.ThroughputUtility
import com.netwatcher.polaris.utils.WebTestUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NetworkRepositoryImpl(
    private val context: Context,
    private val telephonyManager: TelephonyManager,
    private val networkDataDao: NetworkDataDao
) : NetworkRepository {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

//    private val smsTestUtility = SmsTestUtility(context)

    override suspend fun addNetworkData(networkData: NetworkData) {
        networkDataDao.addNetworkData(networkData)
    }
    override fun getAllNetworkData(): Flow<List<NetworkData>> {
        return networkDataDao.getAllNetworkData()
    }
    override suspend fun getNetworkDataById(id: Long): Flow<NetworkData> {
        return networkDataDao.getNetworkDataById(id)
    }
    override suspend fun deleteNetworkData(networkData: NetworkData) {
        networkDataDao.deleteNetworkData(networkData)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCoroutine { cont ->
        if (!LocationUtility.isLocationEnabled(context)) {
            cont.resume(null)
            return@suspendCoroutine
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(location)
                    } else {
                        requestFreshLocation(cont)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Location", "Last location error", e)
                    requestFreshLocation(cont)
                }
        } catch (e: Exception) {
            Log.e("Location", "Location exception", e)
            cont.resume(null)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(cont: Continuation<Location?>) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).setMaxUpdates(1).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                cont.resume(result.lastLocation)
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    fusedLocationClient.removeLocationUpdates(this)
                    cont.resume(null)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            Log.e("Location", "Location updates error", e)
            cont.resume(null)
        }
    }

    override suspend fun pingTest(host: String): Double? = withContext(Dispatchers.IO) {
        try {
            PingUtility.ping(host)
        } catch (e: Exception) {
            Log.e("PingTest", "Error during ping test", e)
            null
        }
    }

    override suspend fun dnsTest(hostname: String): Long? = withContext(Dispatchers.IO) {
        try {
            DnsUtility.measureDnsResolutionWithRetry(hostname)
        } catch (e: Exception) {
            Log.e("DnsTest", "Error during DNS test", e)
            null
        }
    }

    override suspend fun measureUploadThroughput(): Double? {
        return ThroughputUtility.measureUploadThroughput()
    }

    override suspend fun measureWebResponseTime(): Long? = withContext(Dispatchers.IO) {
        val testUrls = listOf(
            "https://www.google.com",
            "https://www.cloudflare.com",
//            "https://quera.org"
        )

        val results = mutableListOf<Long>()

        for (url in testUrls) {
            try {
                val time = WebTestUtility.measureWebResponseTime(url)
                time?.let { results.add(it) }
                if (results.size >= 1) break
            } catch (e: Exception) {
                Log.e("NetworkRepository", "Error measuring web response for $url: ${e.message}")
            }
        }

        results.takeIf { it.isNotEmpty() }?.average()?.toLong()
    }

//    @SuppressLint("MissingPermission")
//    suspend fun measureSmsDeliveryTime(): Long? = withContext(Dispatchers.IO) {
//        smsTestUtility.measureSmsDeliveryTime()
//    }

    @SuppressLint("MissingPermission")
    override suspend fun runNetworkTest(): NetworkData {
        val location = getCurrentLocation()
        val cellInfo = telephonyManager.allCellInfo.firstOrNull { it.isRegistered }
        val netType = getNetworkType(cellInfo)

        val pingTime = pingTest() ?: 0.0
        val dnsTime = dnsTest()?.toInt() ?: 0
        val uploadThroughput = measureUploadThroughput() ?: 0.0
        val webResponseTime = measureWebResponseTime()
//        val smsDeliveryTime = measureSmsDeliveryTime()?.toInt() ?: -1

        val networkData = NetworkData(
            location?.latitude ?: 0.0,
            location?.longitude ?: 0.0,
            SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault()).format(Date()),
            netType,
            getTac(cellInfo),
            getLac(cellInfo),
            getCellId(cellInfo),
            getRac(cellInfo),
            getPlmnId(),
            getArfcn(cellInfo),
            getFrequency(cellInfo),
            getFrequencyBand(cellInfo),
            getRsrp(cellInfo),
            getRsrq(cellInfo),
            getRscp(cellInfo),
            getEcIo(cellInfo),
            getRxLev(cellInfo),
            getSsRsrp(cellInfo),
            uploadThroughput,
//            downloadThroughput,
            -1.0,
            pingTime,
            dnsTime,
            webResponseTime,
//            smsDeliveryTime
            -1
//            -1.0,-1.0,-1,-1,-1
        )

        addNetworkData(networkData)

        return networkData
    }

    private fun getNetworkType(cellInfo: CellInfo?): String {
        return when {
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (cellInfo is CellInfoNr) -> "5G"
            cellInfo is CellInfoLte -> "LTE"
            cellInfo is CellInfoWcdma -> "HSPA"
            cellInfo is CellInfoGsm -> "GSM"
            else -> "UNKNOWN"
        }
    }

    private fun getPlmnId(): String? = telephonyManager.networkOperator

    private fun getTac(cellInfo: CellInfo?): String? {
        val tac = when {
            cellInfo is CellInfoLte -> cellInfo.cellIdentity.tac.toString()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> "N/A"
            else -> null
        }
        return tac
    }

    private fun getLac(cellInfo: CellInfo?): String? {
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

    private fun getCellId(cellInfo: CellInfo?): String? {
        return when {
            cellInfo is CellInfoGsm -> cellInfo.cellIdentity.cid.toString()
            cellInfo is CellInfoLte -> cellInfo.cellIdentity.ci.toString()
            cellInfo is CellInfoWcdma -> cellInfo.cellIdentity.cid.toString()
            else -> handlePossibleNrCell(cellInfo)
        }
    }

    private fun handlePossibleNrCell(cellInfo: CellInfo?): String? {
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

    // may break!
    private fun getRac(cellInfo: CellInfo?): String? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> null
            cellInfo is CellInfoLte -> null

            cellInfo is CellInfoGsm -> {
                try {
                    val racField = cellInfo.cellIdentity.javaClass.getDeclaredField("mRac")
                    racField.isAccessible = true
                    racField.get(cellInfo.cellIdentity)?.toString()
                } catch (e: Exception) {
                    null
                }
            }

            cellInfo is CellInfoWcdma -> {
                try {
                    val racField = cellInfo.cellIdentity.javaClass.getDeclaredField("mRac")
                    racField.isAccessible = true
                    racField.get(cellInfo.cellIdentity)?.toString()
                } catch (e: Exception) {
                    null
                }
            }

            else -> null
        }
    }

    private fun getRsrp(cellInfo: CellInfo?): Int? {
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

    private fun getRsrq(cellInfo: CellInfo?): Int? {
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

    private fun getRscp(cellInfo: CellInfo?): Int? {
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

    private fun getEcIo(cellInfo: CellInfo?): Int? {
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

    private fun getRxLev(cellInfo: CellInfo?): Int? {
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

    private fun getSsRsrp(cellInfo: CellInfo?): Int? {
        return when {
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (cellInfo is CellInfoNr) -> (cellInfo.cellSignalStrength as? CellSignalStrengthNr)?.ssRsrp
            else -> null
        }
    }

    private fun getArfcn(cellInfo: CellInfo?): Int? {
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

    private fun getFrequency(cellInfo: CellInfo?): Double? {
        val arfcn = getArfcn(cellInfo) ?: return null

        return when {
            cellInfo is CellInfoGsm -> {
                if (arfcn >= 0 && arfcn <= 124) 935.0 + 0.2 * arfcn // GSM 900
                else 1805.0 + 0.2 * (arfcn - 512) // GSM 1800
            }

            cellInfo is CellInfoLte -> {
                if (arfcn >= 0 && arfcn <= 599) 2110.0 - 0.1 * (arfcn - 18000)
                else 1930.0 - 0.1 * (arfcn - 1575)
            }

            cellInfo is CellInfoWcdma -> {
                2110.0 - 0.1 * (arfcn - 10562)
            }

            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && (cellInfo is CellInfoNr) -> {
                if (arfcn >= 0 && arfcn <= 2016667) 0.001 * arfcn // Formula varies by band
                else null
            }

            else -> null
        }
    }

    private fun getFrequencyBand(cellInfo: CellInfo?): String? {
        val arfcn = getArfcn(cellInfo) ?: return null

        return when {
            cellInfo is CellInfoGsm -> {
                when {
                    arfcn in 0..124 -> "GSM 900"
                    arfcn in 975..1023 -> "GSM 900 (Extended)"
                    arfcn in 128..251 -> "GSM 850"
                    arfcn in 512..885 -> "GSM 1800"
                    arfcn in 512..810 -> "GSM 1900"
                    else -> "GSM Unknown"
                }
            }

            cellInfo is CellInfoLte -> "LTE Band (EARFCN: $arfcn)"
            cellInfo is CellInfoWcdma -> "WCDMA Band (UARFCN: $arfcn)"

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr -> {
                try {
                    val method = cellInfo.cellIdentity.javaClass.getMethod("getNrarfcn")
                    val nrarfcn = method.invoke(cellInfo.cellIdentity) as? Int

                    val bwMethod = cellInfo.cellIdentity.javaClass.getMethod("getBandwidth")
                    val bandwidth = bwMethod.invoke(cellInfo.cellIdentity) as? Int

                    "NR Band (NRARFCN: $nrarfcn, Bandwidth: ${bandwidth ?: "Unknown"})"
                } catch (e: Exception) {
                    "NR Band (Unknown)"
                }
            }

            else -> null
        }
    }
}