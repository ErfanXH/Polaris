package com.netwatcher.polaris.utils

import android.content.Context
import android.preference.PreferenceManager

object TestConfigManager {
    const val KEY_SMS_TEST_NUMBER = "sms_test_number"
    const val KEY_PING_TEST_ADDRESS = "ping_test_address"
    const val KEY_DNS_TEST_ADDRESS = "dns_test_address"
    const val KEY_WEB_TEST_ADDRESS = "web_test_address"

    fun getPreferences(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun setSmsTestNumber(context: Context, number: String) {
        getPreferences(context).edit()
            .putString(KEY_SMS_TEST_NUMBER, number)
            .apply()
    }

    fun setPingTestAddress(context: Context, address: String) {
        getPreferences(context).edit()
            .putString(KEY_PING_TEST_ADDRESS, address)
            .apply()
    }

    fun setDnsTestAddress(context: Context, address: String) {
        getPreferences(context).edit()
            .putString(KEY_DNS_TEST_ADDRESS, address)
            .apply()
    }

    const val DEFAULT_WEB_TEST_URL = "https://www.google.com"

    fun setWebTestAddress(context: Context, address: String) {
        val preparedUrl = when {
            address.isBlank() -> DEFAULT_WEB_TEST_URL
            address.startsWith("http://") || address.startsWith("https://") -> address
            address.startsWith("www.") -> "https://$address"
            else -> "https://$address"
        }

        getPreferences(context).edit()
            .putString(KEY_WEB_TEST_ADDRESS, preparedUrl)
            .apply()
    }

    fun initializeDefaults(context: Context) {
        val prefs = getPreferences(context)
        if (!prefs.contains(KEY_WEB_TEST_ADDRESS)) {
            prefs.edit().putString(KEY_WEB_TEST_ADDRESS, DEFAULT_WEB_TEST_URL).apply()
        }
    }
}