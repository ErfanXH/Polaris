package com.netwatcher.polaris.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
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
import com.netwatcher.polaris.data.remote.NetworkDataApi
import com.netwatcher.polaris.di.CookieManager
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.NetworkDataDao
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.domain.repository.NetworkRepository
import com.netwatcher.polaris.utils.*
import com.netwatcher.polaris.utils.measurements.getCellInfo
import com.netwatcher.polaris.utils.tests.DnsUtility
import com.netwatcher.polaris.utils.tests.HttpDownloadUtility
import com.netwatcher.polaris.utils.tests.HttpUploadUtility
import com.netwatcher.polaris.utils.tests.PingUtility
import com.netwatcher.polaris.utils.tests.SmsTestUtility
import com.netwatcher.polaris.utils.tests.WebTestUtility
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
    override suspend fun uploadNetworkDataBatch(data: RequestBody): Result<Unit> {
        return try {
            val response =
                api.uploadNetworkDataBatch(token = getAuthToken().toString(), data = data)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage =
                    response.errorBody()?.string() ?: "Failed to Sync Batch Data with Server"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getAuthToken(): String? {
        return CookieManager.getToken().firstOrNull()
    }

    private suspend fun getAuthEmail(): String? {
        return CookieManager.getEmail().firstOrNull()
    }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCoroutine { cont ->
        if (!LocationUtility.isLocationEnabled(context)) {
            Log.w("NetworkRepository", "Location is disabled, continue with previous...")
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

    @SuppressLint("MissingPermission")
    override suspend fun runNetworkTest(
        simSlotIndex: Int,
        subscriptionId: Int,
        testSelection: TestSelection
    ): NetworkData {
        try{
            val location = getCurrentLocation()

            val sm =
                context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            val allCellsInfo = try {
                tm.allCellInfo?.filter { it.isRegistered } ?: emptyList()
            } catch (e: Exception) {
                Log.e("NetworkTest", "Error getting cell info", e)
                emptyList()
            }

            if (allCellsInfo.isEmpty()) {
                Log.w("NetworkTest", "No cell info available")
                return NetworkData.empty()
            }

            val subscriptionList = sm.activeSubscriptionInfoList ?: emptyList()

            val networkTypeInt = tm.createForSubscriptionId(subscriptionId).dataNetworkType
            val networkType = networkTypeToString(networkTypeInt)

            val subInfo = sm.getActiveSubscriptionInfoForSimSlotIndex(simSlotIndex)

            val targetCell: CellInfo? =
                if (subscriptionList.size == 1) {
                    allCellsInfo?.firstOrNull()
                } else {
                    val indexInList =
                        subscriptionList.indexOfFirst { it.simSlotIndex == subInfo.simSlotIndex }
                    allCellsInfo?.getOrNull(indexInList)
                }

            val res = getCellInfo(targetCell, networkType)

            val httpUploadThroughput =
                if (testSelection.runUploadTest) measureUploadThroughput() else -1.0
            val httpDownloadThroughput =
                if (testSelection.runDownloadTest) measureDownloadThroughput() else -1.0
            val pingTime = if (testSelection.runPingTest) pingTest() else -1.0
            val dnsResponse = if (testSelection.runDnsTest) dnsTest() else -1.0
            val webResponse = if (testSelection.runWebTest) measureWebResponseTime() else -1.0
            val smsDeliveryTime =
                if (testSelection.runSmsTest) measureSmsDeliveryTime()?.toDouble() else -1.0

            val networkData = NetworkData(
                location?.latitude ?: -1.0,
                location?.longitude ?: -1.0,
                SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault()).format(Date()),
                res?.networkType,
                res?.tac, res?.lac, res?.cellId, tm.networkOperator,
                res?.arfcn, res?.frequency, res?.frequencyBand,
                res?.rsrp, res?.rsrq, res?.rscp, res?.ecIo, res?.rxLev,
                httpUploadThroughput ?: -1.0,
                httpDownloadThroughput ?: -1.0,
                pingTime ?: -1.0,
                dnsResponse ?: -1.0,
                webResponse ?: -1.0,
                smsDeliveryTime ?: -1.0,
                getAuthEmail()
            )

            if (isValidNetworkData(networkData, targetCell)) {
                addNetworkData(networkData)
            }
            return networkData
        }
        catch (e: Exception) {
            return NetworkData.invalid()
        }
    }

    private fun isValidNetworkData(networkData: NetworkData?, cellInfo: CellInfo?): Boolean {
        return !(cellInfo == null ||
                networkData == null ||
                networkData.latitude == -1.0 ||
                networkData.longitude == -1.0 ||
                networkData.networkType == "Others")
    }

    private fun networkTypeToString(networkType: Int): String {
        return when (networkType) {
            NETWORK_TYPE_GPRS -> "GPRS"
            NETWORK_TYPE_EDGE -> "EDGE"
            NETWORK_TYPE_CDMA -> "CDMA"
            NETWORK_TYPE_UMTS -> "UMTS"
            NETWORK_TYPE_HSDPA -> "HSDPA"
            NETWORK_TYPE_HSUPA -> "HSUPA"
            NETWORK_TYPE_HSPA -> "HSPA"
            NETWORK_TYPE_HSPAP -> "HSPA+"
            NETWORK_TYPE_LTE -> "LTE"
            NETWORK_TYPE_NR -> "5G"
            else -> "Others"
        }
    }
}