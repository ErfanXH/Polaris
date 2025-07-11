package com.netwatcher.polaris.domain.model

import com.google.gson.annotations.SerializedName

data class VerifyResetCodeRequest(
    @SerializedName("number_or_email") val numberOrEmail: String,
    val code: String
)