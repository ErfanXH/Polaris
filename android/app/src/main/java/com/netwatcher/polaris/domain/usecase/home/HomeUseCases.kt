package com.netwatcher.polaris.domain.usecase.home

import javax.inject.Inject

data class HomeUseCases @Inject constructor(
    val selectedSim: SelectSimUseCase,
    val runNetworkTest: RunNetworkTestUseCase,
    val loadInitialState: LoadInitialStateUseCase,
    val logout: LogoutUseCase
)