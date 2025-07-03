package com.netwatcher.polaris.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TestSelection(
    val runUploadTest: Boolean = true,
    val runDownloadTest: Boolean = true,
    val runPingTest: Boolean = true,
    val runDnsTest: Boolean = true,
    val runWebTest: Boolean = true,
    val runSmsTest: Boolean = true
) : Parcelable