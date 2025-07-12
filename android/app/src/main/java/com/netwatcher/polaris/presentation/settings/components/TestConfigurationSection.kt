package com.netwatcher.polaris.presentation.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.presentation.settings.SettingsViewModel
import com.netwatcher.polaris.utils.TestConfigManager

@Composable
fun TestConfigurationSection(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val prefs = remember { TestConfigManager.getPreferences(context) }

    var smsTestNumber by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_SMS_TEST_NUMBER, "+989303009264") ?: "")
    }
    var pingTestAddress by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_PING_TEST_ADDRESS, "8.8.8.8") ?: "")
    }
    var dnsTestAddress by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_DNS_TEST_ADDRESS, "google.com") ?: "")
    }
    var webTestAddress by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_WEB_TEST_ADDRESS, "https://www.google.com") ?: "")
    }

    Column {
        Text("Test Configuration", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = smsTestNumber,
            onValueChange = {
                smsTestNumber = it
                viewModel.setSmsTestNumber(it)
            },
            label = { Text("SMS Test Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pingTestAddress,
            onValueChange = {
                pingTestAddress = it
                viewModel.setPingAddress(it)
            },
            label = { Text("Ping Test Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dnsTestAddress,
            onValueChange = {
                dnsTestAddress = it
                viewModel.setDnsAddress(it)
            },
            label = { Text("DNS Test Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = webTestAddress,
            onValueChange = {
                webTestAddress = it
                viewModel.setWebAddress(it)
            },
            label = { Text("Web Test Address") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}