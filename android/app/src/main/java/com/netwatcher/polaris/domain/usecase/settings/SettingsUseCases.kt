package com.netwatcher.polaris.domain.usecase.settings

import com.netwatcher.polaris.domain.usecase.home.SelectSimUseCase
import javax.inject.Inject

data class SettingsUseCases @Inject constructor(
    val testConfig: TestConfigUseCases,
    val updateSyncInterval: UpdateSyncIntervalUseCase,
    val loadSimCards: LoadSimCardsUseCase,
)
