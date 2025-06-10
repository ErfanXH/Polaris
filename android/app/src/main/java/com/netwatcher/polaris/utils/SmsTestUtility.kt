package eh.learning.homepage.utils

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
    private var smsContinuation: Continuation<Long>? = null
    private var smsStartTime: Long = 0
    private val smsSentAction = "${context.packageName}.SMS_SENT_ACTION"
    private val smsDeliveredAction = "${context.packageName}.SMS_DELIVERED_ACTION"
    private val testPhoneNumber = "+989024004546"

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

    @SuppressLint("MissingPermission")
    suspend fun measureSmsDeliveryTime(): Long? = suspendCoroutine { cont ->
        try {
            registerReceivers()
            smsContinuation = cont
            smsStartTime = System.currentTimeMillis()

            val sentIntent = createPendingIntent(smsSentAction)
            val deliveredIntent = createPendingIntent(smsDeliveredAction)

            getSmsManager().sendTextMessage(
                testPhoneNumber,
                null,
                "Network test SMS",
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
//        context.registerReceiver(
//            smsSentReceiver,
//            IntentFilter(smsSentAction),
//            Context.RECEIVER_EXPORTED
//        )

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

//        context.registerReceiver(
//            smsDeliveredReceiver,
//            IntentFilter(smsDeliveredAction)
//        )
    }

    private fun setupTimeout() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (smsContinuation != null) {
                completeWithError("SMS delivery timeout")
            }
        }, 10000)
    }

    private fun completeWithSuccess(deliveryTime: Long) {
        smsContinuation?.resume(deliveryTime)
        smsContinuation = null
        cleanup()
    }

    private fun completeWithError(errorMessage: String) {
        Log.e("SmsTestUtility", errorMessage)
        smsContinuation?.resume(-1)
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