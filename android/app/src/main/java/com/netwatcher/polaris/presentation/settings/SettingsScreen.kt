package com.netwatcher.polaris.presentation.settings

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netwatcher.polaris.domain.model.SimInfo

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun SettingsScreen(
    onSimSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val subscriptionManager = remember {
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    val simList = remember {
        subscriptionManager.activeSubscriptionInfoList?.map {
            SimInfo(
                displayName = it.displayName?.toString() ?: "Unknown",
                carrierName = it.carrierName?.toString() ?: "Unknown",
                simSlotIndex = it.simSlotIndex,
                subscriptionId = it.subscriptionId
            )
        } ?: emptyList()
    }


    var selectedSimId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Select SIM Card", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            simList.forEachIndexed { index, sim ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SIM Slot: ${sim.simSlotIndex + 1} (${sim.carrierName})", style = MaterialTheme.typography.labelLarge)
                        RadioButton(
                            selected = sim.subscriptionId == selectedSimId,
                            onClick = {
                                selectedSimId = sim.subscriptionId
                                onSimSelected(sim.subscriptionId)
                            }
                        )
                    }

                    if (index != simList.lastIndex) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}
