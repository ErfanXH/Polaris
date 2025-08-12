package com.netwatcher.polaris.domain.model

import com.google.gson.annotations.SerializedName

data class VerificationRequest(
    @SerializedName("number_or_email") val numberOrEmail: String,
    val password: String,
    val code: String,
)
