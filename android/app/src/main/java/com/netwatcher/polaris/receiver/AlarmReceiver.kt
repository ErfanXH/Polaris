package com.netwatcher.polaris.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.netwatcher.polaris.service.TestExecutionService
import com.netwatcher.polaris.utils.TestAlarmScheduler

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm triggered or Boot completed: ${intent?.action}")

        // Re-schedule test on boot
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("AlarmReceiver", "Boot completed — rescheduling test alarm.")
            TestAlarmScheduler.scheduleTest(context)
            return
        }

        // Start the test execution service
        val serviceIntent = Intent(context, TestExecutionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Schedule the next test
        TestAlarmScheduler.scheduleTest(context)
    }
}
