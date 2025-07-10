package com.netwatcher.polaris.utils

import android.content.Context
import android.preference.PreferenceManager
import com.netwatcher.polaris.domain.model.TestSelection

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

    private const val KEY_RUN_UPLOAD_TEST = "run_upload_test"
    private const val KEY_RUN_DOWNLOAD_TEST = "run_download_test"
    private const val KEY_RUN_PING_TEST = "run_ping_test"
    private const val KEY_RUN_DNS_TEST = "run_dns_test"
    private const val KEY_RUN_WEB_TEST = "run_web_test"
    private const val KEY_RUN_SMS_TEST = "run_sms_test"

    fun getTestSelection(context: Context): TestSelection {
        val prefs = getPreferences(context)
        return TestSelection(
            runUploadTest = prefs.getBoolean(KEY_RUN_UPLOAD_TEST, true),
            runDownloadTest = prefs.getBoolean(KEY_RUN_DOWNLOAD_TEST, true),
            runPingTest = prefs.getBoolean(KEY_RUN_PING_TEST, true),
            runDnsTest = prefs.getBoolean(KEY_RUN_DNS_TEST, true),
            runWebTest = prefs.getBoolean(KEY_RUN_WEB_TEST, true),
            runSmsTest = prefs.getBoolean(KEY_RUN_SMS_TEST, true)
        )
    }

    fun setTestSelection(context: Context, selection: TestSelection) {
        getPreferences(context).edit()
            .putBoolean(KEY_RUN_UPLOAD_TEST, selection.runUploadTest)
            .putBoolean(KEY_RUN_DOWNLOAD_TEST, selection.runDownloadTest)
            .putBoolean(KEY_RUN_PING_TEST, selection.runPingTest)
            .putBoolean(KEY_RUN_DNS_TEST, selection.runDnsTest)
            .putBoolean(KEY_RUN_WEB_TEST, selection.runWebTest)
            .putBoolean(KEY_RUN_SMS_TEST, selection.runSmsTest)
            .apply()
    }

    private const val KEY_SELECTED_SIM_ID = "selected_sim_id"

    fun getSelectedSimId(context: Context): Int? {
        val prefs = getPreferences(context)
        return if (prefs.contains(KEY_SELECTED_SIM_ID)) {
            prefs.getInt(KEY_SELECTED_SIM_ID, -1).takeIf { it != -1 }
        } else {
            null
        }
    }

    fun setSelectedSimId(context: Context, simId: Int?) {
        getPreferences(context).edit()
            .putInt(KEY_SELECTED_SIM_ID, simId ?: -1)
            .apply()
    }

    fun initializeDefaults(context: Context) {
        val prefs = getPreferences(context)
        if (!prefs.contains(KEY_RUN_UPLOAD_TEST)) {
            setTestSelection(context, TestSelection())
        }
        if (!prefs.contains(KEY_WEB_TEST_ADDRESS)) {
            prefs.edit().putString(KEY_WEB_TEST_ADDRESS, DEFAULT_WEB_TEST_URL).apply()
        }
    }
}