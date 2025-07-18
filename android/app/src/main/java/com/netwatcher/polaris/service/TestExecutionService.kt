package com.netwatcher.polaris.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.netwatcher.polaris.data.local.AppDatabaseHelper
import com.netwatcher.polaris.data.repository.NetworkRepositoryImpl
import com.netwatcher.polaris.di.NetworkModule
import com.netwatcher.polaris.di.CookieManager
import com.netwatcher.polaris.utils.TestConfigManager
import com.netwatcher.polaris.utils.*
import com.netwatcher.polaris.utils.LocationUtility.isLocationEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TestExecutionService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var repository: NetworkRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        Log.d("TestExecutionService", "Service created.")
        val dao = AppDatabaseHelper.getDatabase(this).networkDataDao()
        repository = NetworkRepositoryImpl(this, dao, NetworkModule.networkDataApi)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TestExecutionService", "Starting foreground service for test execution...")

        if (!hasAllPermissions(this)) {
            Log.w(
                "TestExecutionService",
                "Required permissions are missing, aborting test execution."
            )
            stopSelf()
            return START_NOT_STICKY
        }

        if (!isLocationEnabled(this)) {
            Log.w("TestExecutionService", "Location is disabled, stopping service...")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIF_ID, createNotification())

        val testSelection = TestConfigManager.getTestSelection(this)
        val selectedSimSlotId = TestConfigManager.getSelectedSimSlotId(this)
        val selectedSimSubsId = TestConfigManager.getSelectedSimSubsId(this)

        serviceScope.launch {
            try {
                val token = CookieManager.getToken().first()

                if (!token.isNullOrEmpty()) {
                    Log.d(
                        "TestExecutionService",
                        "Running network test for $selectedSimSlotId in background..."
                    )
                    repository.runNetworkTest(
                        testSelection = testSelection,
                        simSlotIndex = selectedSimSlotId ?: 0,
                        subscriptionId = selectedSimSubsId ?: -1
                    )
                    Log.d("TestExecutionService", "Network test finished and saved locally.")
                } else {
                    Log.d("TestExecutionService", "No token available - skipping network test")
                }
            } catch (e: Exception) {
                Log.e("TestExecutionService", "Error during background test: ${e.message}", e)
            } finally {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "network_test_channel"
        val channel = NotificationChannel(
            channelId,
            "Network Test Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Polaris Test Running")
            .setContentText("Executing network test in background.")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
        Log.d("TestExecutionService", "Service destroyed.")
    }

    companion object {
        private const val NOTIF_ID = 102
    }
}
