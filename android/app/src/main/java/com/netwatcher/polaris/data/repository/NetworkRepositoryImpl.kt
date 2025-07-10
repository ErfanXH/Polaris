package com.netwatcher.polaris.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Looper
import android.telephony.*
import android.telephony.TelephonyManager.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.netwatcher.polaris.data.remote.NetworkDataApi
import com.netwatcher.polaris.di.TokenManager
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.NetworkDataDao
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NetworkRepositoryImpl(
    private val context: Context,
    private val defaultTelephonyManager: TelephonyManager,
    val networkDataDao: NetworkDataDao,
    private val api: NetworkDataApi
) : NetworkRepository {
    // Local Database
    override suspend fun addNetworkData(networkData: NetworkData) {
        networkDataDao.addNetworkData(networkData)
    }
    override fun getAllNetworkData(): Flow<List<NetworkData>> {
        return networkDataDao.getAllNetworkData()
    }

    // Server Database
    override suspend fun uploadNetworkData(data: Any): Result<Unit> {
        return try {
            val response = api.uploadNetworkData(token = getAuthToken().toString(), data = data)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to Sync Data with Server"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun uploadNetworkDataBatch(data: RequestBody): Result<Unit> {
        return try {
            val response = api.uploadNetworkDataBatch(token = getAuthToken().toString(), data = data)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to Sync Batch Data with Server"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getUserInfo(): Result<Unit> {
        return try {
            val response = api.getUserInfo(token = getAuthToken().toString())
            if (response.isSuccessful) {
                NetworkDataDao.setEmail(response.body()?.email)
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Failed to Get User Info"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private suspend fun getAuthToken(): String? {
        return TokenManager.getToken().firstOrNull()
    }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
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

    override suspend fun measureUploadThroughput(): Double {
        return HttpUploadUtility.measureUploadThroughput()
    }

    override suspend fun measureDownloadThroughput(): Double {
        return HttpDownloadUtility.measureDownloadThroughput()
    }

    override suspend fun pingTest(): Double? = withContext(Dispatchers.IO) {
        val testHost = TestConfigManager.getPreferences(context)
            .getString(TestConfigManager.KEY_PING_TEST_ADDRESS, "8.8.8.8") ?: "8.8.8.8"
        try {
            PingUtility.ping(testHost)
        } catch (e: Exception) {
            Log.e("PingTest", "Error during ping test", e)
            null
        }
    }

    override suspend fun dnsTest(): Double? = withContext(Dispatchers.IO) {
        val testHost = TestConfigManager.getPreferences(context)
            .getString(TestConfigManager.KEY_DNS_TEST_ADDRESS, "google.com") ?: "google.com"
        try {
            DnsUtility.measureDnsResolutionWithRetry(testHost)
        } catch (e: Exception) {
            Log.e("DnsTest", "Error during DNS test", e)
            null
        }
    }

    override suspend fun measureWebResponseTime(): Double? = withContext(Dispatchers.IO) {
        val testUrl = TestConfigManager.getPreferences(context)
            .getString(TestConfigManager.KEY_WEB_TEST_ADDRESS, null)

        try {
            WebTestUtility.measureWebResponseTime(testUrl).also { result ->
                if (result == null) {
                    Log.e("WebTest", "Web response test failed for URL: ${testUrl ?: "null"}")
                }
            }
        } catch (e: Exception) {
            Log.e("WebTest", "Error during web response test", e)
            null
        }
    }

    private val smsTestUtility = SmsTestUtility(context)
    @SuppressLint("MissingPermission")
    suspend fun measureSmsDeliveryTime(): Double? = withContext(Dispatchers.IO) {
        smsTestUtility.measureSmsDeliveryTime(context)?.toDouble()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    override suspend fun runNetworkTest(subscriptionId: Int?, testSelection: TestSelection): NetworkData {
        Log.d("SIM ID", subscriptionId.toString())
        val tm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && subscriptionId != null) {
            defaultTelephonyManager.createForSubscriptionId(subscriptionId)
        } else {
            defaultTelephonyManager
        }
        val location = getCurrentLocation()
        val cellInfo = tm.allCellInfo.firstOrNull { it.isRegistered }
        Log.d("networkTypeCellInfo", "Raw CellInfo: ${cellInfo?.toString()}")
//        val netType = getNetworkType(cellInfo)

        val networkTypeInt = tm.dataNetworkType
        Log.d("networkTypeInt", networkTypeInt.toString())
        val networkType = networkTypeToString(networkTypeInt)
        Log.d("networkTypeToString", networkType)

        val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subscriptionList = subManager.activeSubscriptionInfoList ?: emptyList()

        Log.d("subscriptionId", subscriptionId.toString())
        val simSlotIndex = if (subscriptionId == 1) 1 else 0
        val subInfo = subManager.getActiveSubscriptionInfoForSimSlotIndex(simSlotIndex)
        val subId = subInfo.subscriptionId

        val cellInfos = tm.allCellInfo
        val targetCell: CellInfo? =
            if (subscriptionList.size == 1) {
                cellInfos?.firstOrNull()
            } else {
                val indexInList = subscriptionList.indexOfFirst { it.subscriptionId == subId }
                cellInfos?.getOrNull(indexInList)
            }

        val httpUploadThroughput = if (testSelection.runUploadTest) measureUploadThroughput() else -1.0
        val httpDownloadThroughput = if (testSelection.runDownloadTest) measureDownloadThroughput() else -1.0
        val pingTime = if (testSelection.runPingTest) pingTest() else -1.0
        val dnsResponse = if (testSelection.runDnsTest) dnsTest() else -1.0
        val webResponse = if (testSelection.runWebTest) measureWebResponseTime() else -1.0
        val smsDeliveryTime = if (testSelection.runSmsTest) measureSmsDeliveryTime()?.toDouble() else -1.0

        var actualTech : String = ""

        if (targetCell == null) {
            Log.e("CellInfoCollector", "No matching cell info found")
        } else {
            actualTech = when (targetCell) {
                is CellInfoLte -> "LTE"
                is CellInfoWcdma -> {
                    "WCDMA"
                }

                is CellInfoGsm -> {
                    "GSM"
                }

                is CellInfoCdma -> "CDMA"
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetCell.javaClass.simpleName == "CellInfoNr") {
                        "NR"
                    } else {
                        "Unknown"
                    }
                }
            }
        }

        val ss = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            targetCell?.cellSignalStrength
        } else {
            TODO("VERSION.SDK_INT < R")
        }

        val networkData = NetworkData(
            location?.latitude ?: -1.0,
            location?.longitude ?: -1.0,
            SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault()).format(Date()),
            actualTech,
            getTac(cellInfo),
            getLac(cellInfo),
            getCellId(cellInfo),
            null,
            tm.networkOperator,
            getArfcn(cellInfo),
            getFrequency(cellInfo),
            getFrequencyBand(cellInfo),
            getRsrp(cellInfo),
            getRsrq(cellInfo),
            getRscp(cellInfo),
            null,
            getRxLev(cellInfo),
            getSsRsrp(cellInfo),
            httpUploadThroughput,
            httpDownloadThroughput,
            pingTime,
            dnsResponse,
            webResponse,
            smsDeliveryTime,
            NetworkDataDao.getEmail()
        )

        println("Network Data: $networkData")

        if (isValidNetworkData(networkData, cellInfo)) {
            addNetworkData(networkData)
        }

        return networkData
    }

    private fun isValidNetworkData(networkData: NetworkData?, cellInfo: CellInfo?) : Boolean {
        return !(cellInfo == null ||
                networkData == null ||
                networkData.latitude == -1.0 ||
                networkData.longitude == -1.0 ||
                networkData.networkType == "UNKNOWN")
    }

    private fun networkTypeToString(networkType: Int): String {
        return when (networkType) {
            NETWORK_TYPE_GPRS -> "GPRS"
            NETWORK_TYPE_EDGE -> "EDGE"
            NETWORK_TYPE_UMTS -> "UMTS"
            NETWORK_TYPE_CDMA -> "CDMA"
            NETWORK_TYPE_EVDO_0 -> "EVDO rev.0"
            NETWORK_TYPE_EVDO_A -> "EVDO rev.A"
            NETWORK_TYPE_EVDO_B -> "EVDO rev.B"
            NETWORK_TYPE_1xRTT -> "1xRTT"
            NETWORK_TYPE_HSDPA -> "HSDPA"
            NETWORK_TYPE_HSUPA -> "HSUPA"
            NETWORK_TYPE_HSPA -> "HSPA"
            NETWORK_TYPE_HSPAP -> "HSPA+"
            NETWORK_TYPE_LTE -> "LTE"
            NETWORK_TYPE_NR -> "NR"
            NETWORK_TYPE_EHRPD -> "eHRPD"
            NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA"
            NETWORK_TYPE_IWLAN -> "IWLAN"
            else -> "Unknown ($networkType)"
        }
    }
}