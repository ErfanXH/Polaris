package com.netwatcher.polaris.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SmsTestUtility(private val context: Context) {
    private var smsContinuation: Continuation<Double>? = null
    private var smsStartTime: Double = 0.0
    private val smsSentAction = "${context.packageName}.SMS_SENT_ACTION"
    private val smsDeliveredAction = "${context.packageName}.SMS_DELIVERED_ACTION"

    private val smsSentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d("SmsTestUtility", "SMS sent successfully")
                }
                else -> {
                    completeWithError("SMS sending failed")
                }
            }
        }
    }

    private val smsDeliveredReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val deliveryTime = System.currentTimeMillis() - smsStartTime
                    completeWithSuccess(deliveryTime)
                    Log.d("SmsTestUtility", "SMS delivered in $deliveryTime ms")
                }
                else -> {
                    completeWithError("SMS delivery failed")
                }
            }
        }
    }

    private fun getTestPhoneNumber(context: Context): String {
        val defaultNumber = "+989303009264"
        val number = TestConfigManager.getPreferences(context)
            .getString(TestConfigManager.KEY_SMS_TEST_NUMBER, defaultNumber) ?: defaultNumber

        return if (number.isNotBlank()) {
            val correctedNumber = prepareTestPhoneNumber(number)

            if (correctedNumber.isNullOrBlank()) {
                Log.w("SmsTestUtility", "Invalid phone number, using default")
                defaultNumber
            } else {
                correctedNumber
            }
        } else {
            defaultNumber
        }
    }

    private fun prepareTestPhoneNumber(number: String) : String? {
        val digitsOnly = number.replace(Regex("[^0-9]"), "")

        return when {
            // Case 1: Already in +98 format (e.g. +989303009264)
            number.startsWith("+98") && digitsOnly.length == 11 -> number

            // Case 2: Starts with 98 (e.g. 989303009264)
            digitsOnly.startsWith("98") && digitsOnly.length == 11 -> "+$digitsOnly"

            // Case 3: Starts with 0 (e.g. 09303009264)
            digitsOnly.startsWith("0") && digitsOnly.length == 11 -> {
                "+98" + digitsOnly.substring(1)
            }

            // Case 4: No prefix, 9 digits (e.g. 9303009264)
            digitsOnly.length == 10 -> "+98$digitsOnly"

            else -> {
                null
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun measureSmsDeliveryTime(context: Context): Double? = suspendCoroutine { cont ->
        try {
            registerReceivers()
            smsContinuation = cont
            smsStartTime = System.currentTimeMillis().toDouble()

            val phoneNumber = getTestPhoneNumber(context).also {
                if (it.isBlank()) throw IllegalArgumentException("Empty phone number")
            }

            Log.d("SmsTestUtility", "Sending SMS to: $phoneNumber")

            val sentIntent = createPendingIntent(smsSentAction)
            val deliveredIntent = createPendingIntent(smsDeliveredAction)

            getSmsManager().sendTextMessage(
                phoneNumber,
                null,
                "This is a test for SMS delivery time from POLARIS...",
                sentIntent,
                deliveredIntent
            )

            setupTimeout()
        } catch (e: Exception) {
            Log.e("SmsTestUtility", "SMS test failed", e)
            cont.resume(null)
            cleanup()
        }
    }

    private fun createPendingIntent(action: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            0,
            Intent(action),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getSmsManager(): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }
    }

    private fun registerReceivers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(smsSentReceiver, IntentFilter(smsSentAction), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(smsSentReceiver, IntentFilter(smsSentAction))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(smsDeliveredReceiver, IntentFilter(smsDeliveredAction), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(smsDeliveredReceiver, IntentFilter(smsDeliveredAction))
        }
    }

    private fun setupTimeout() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (smsContinuation != null) {
                completeWithError("SMS delivery timeout")
            }
        }, 10000)
    }

    private fun completeWithSuccess(deliveryTime: Double) {
        smsContinuation?.resume(deliveryTime)
        smsContinuation = null
        cleanup()
    }

    private fun completeWithError(errorMessage: String) {
        Log.e("SmsTestUtility", errorMessage)
        smsContinuation?.resume(-1.0)
        smsContinuation = null
        cleanup()
    }

    private fun cleanup() {
        try {
            context.unregisterReceiver(smsSentReceiver)
            context.unregisterReceiver(smsDeliveredReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered, ignore
        }
    }
}