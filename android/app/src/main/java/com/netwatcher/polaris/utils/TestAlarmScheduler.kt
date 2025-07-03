package com.netwatcher.polaris.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.netwatcher.polaris.receiver.AlarmReceiver

/**
 * Utility object for scheduling the background network test execution using AlarmManager.
 */
object TestAlarmScheduler {
    private const val ALARM_REQUEST_CODE = 1001
    private const val TEST_INTERVAL_MINUTES = 5L // Interval for background tests

    /**
     * Schedules a precise alarm to trigger the AlarmReceiver.
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerAtMillis = System.currentTimeMillis() + TEST_INTERVAL_MINUTES * 60 * 1000L

        // Use setExactAndAllowWhileIdle for precision even in Doze mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )

        Log.d("TestAlarmScheduler", "Next test alarm set for $TEST_INTERVAL_MINUTES minutes later.")
    }

    /**
     * Cancels any previously scheduled test alarm.
     */
    private fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
        Log.d("TestAlarmScheduler", "Test alarm cancelled.")
    }

    /**
     * Schedules the next test.
     */
    fun scheduleTest(context: Context) {
        schedule(context)
    }

    /**
     * Cancels the existing alarm and schedules a new one.
     * This is used to reset the timer after a manual test run.
     */
    fun rescheduleTest(context: Context) {
        cancel(context)
        schedule(context)
    }
}
