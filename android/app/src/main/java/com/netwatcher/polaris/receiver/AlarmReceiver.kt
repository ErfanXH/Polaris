package com.netwatcher.polaris.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.netwatcher.polaris.service.NetworkForegroundService
import com.netwatcher.polaris.utils.AlarmUtility.scheduleExactAlarm

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm triggered or Boot completed: ${intent?.action}")

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("AlarmReceiver", "Boot completed — rescheduling alarm.")
            scheduleExactAlarm(context)
            return
        }

        val serviceIntent = Intent(context, NetworkForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        scheduleExactAlarm(context)
    }

}