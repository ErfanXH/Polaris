package com.netwatcher.polaris.domain.usecase.settings

import android.content.Context
import com.netwatcher.polaris.utils.DataSyncScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UpdateSyncIntervalUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getCurrentInterval(): Long {
        return DataSyncScheduler.getPreferences(context)
            .getLong(DataSyncScheduler.KEY_SYNC_INTERVAL, 30L)
    }

    fun update(minutes: Long) {
        DataSyncScheduler.updateSyncInterval(context, minutes)
    }
}
