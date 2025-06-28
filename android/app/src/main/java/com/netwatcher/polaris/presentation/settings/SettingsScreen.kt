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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            it.displayName.toString() to it.subscriptionId
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

            simList.forEachIndexed { index, (label, subId) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label)
                    RadioButton(
                        selected = subId == selectedSimId,
                        onClick = {
                            selectedSimId = subId
                            onSimSelected(subId)
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
