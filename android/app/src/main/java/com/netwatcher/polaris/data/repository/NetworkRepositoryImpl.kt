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
    private val telephonyManager: TelephonyManager,
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

    override suspend fun pingTest(host: String): Double? = withContext(Dispatchers.IO) {
        try {
            PingUtility.ping(host)
        } catch (e: Exception) {
            Log.e("PingTest", "Error during ping test", e)
            null
        }
    }

    override suspend fun dnsTest(hostname: String): Double? = withContext(Dispatchers.IO) {
        try {
            DnsUtility.measureDnsResolutionWithRetry(hostname)
        } catch (e: Exception) {
            Log.e("DnsTest", "Error during DNS test", e)
            null
        }
    }

    override suspend fun measureUploadThroughput(): Double {
        return HttpUploadUtility.measureUploadThroughput()
    }

    override suspend fun measureDownloadThroughput(): Double {
        return HttpDownloadUtility.measureDownloadThroughput()
    }

    override suspend fun measureWebResponseTime(): Double? = withContext(Dispatchers.IO) {
        val testUrls = listOf(
            "https://www.google.com",
            "https://quera.org"
        )

        val results = mutableListOf<Double>()

        for (url in testUrls) {
            try {
                val time = WebTestUtility.measureWebResponseTime(url)
                time?.let { results.add(it) }
                if (results.size >= 1) break
            } catch (e: Exception) {
                Log.e("NetworkRepository", "Error measuring web response for $url: ${e.message}")
            }
        }

        results.takeIf { it.isNotEmpty() }?.average()?.toDouble()
    }

//    private val smsTestUtility = SmsTestUtility(context)
//    @SuppressLint("MissingPermission")
//    suspend fun measureSmsDeliveryTime(): Long? = withContext(Dispatchers.IO) {
//        smsTestUtility.measureSmsDeliveryTime()
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    override suspend fun runNetworkTest(): NetworkData {
        val location = getCurrentLocation()
        val cellInfo = telephonyManager.allCellInfo.firstOrNull { it.isRegistered }
        val netType = getNetworkType(cellInfo)

//        val networkTypeInt = telephonyManager.dataNetworkType
//        val netType = networkTypeToString(networkTypeInt)

        val networkData = NetworkData(
            location?.latitude ?: -1.0,
            location?.longitude ?: -1.0,
            SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault()).format(Date()),
            netType,
            getTac(cellInfo),
            getLac(cellInfo),
            getCellId(cellInfo),
            getRac(cellInfo),
            telephonyManager.networkOperator,
            getArfcn(cellInfo),
            getFrequency(cellInfo),
            getFrequencyBand(cellInfo),
            getRsrp(cellInfo),
            getRsrq(cellInfo),
            getRscp(cellInfo),
            getEcIo(cellInfo),
            getRxLev(cellInfo),
            getSsRsrp(cellInfo),
            measureUploadThroughput() ?: -1.0,
            measureDownloadThroughput() ?: -1.0,
            pingTest() ?: -1.0,
            dnsTest() ?: -1.0,
            measureWebResponseTime() ?: -1.0,
//            measureSmsDeliveryTime() ?: -1.0
            -1.0,
            NetworkDataDao.getEmail()
        )

        if (isValidNetworkData(networkData, cellInfo)) {
            addNetworkData(networkData)
//            return networkData
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
}