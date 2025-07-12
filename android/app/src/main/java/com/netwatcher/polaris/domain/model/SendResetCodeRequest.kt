package com.netwatcher.polaris.domain.model

import com.google.gson.annotations.SerializedName

data class SendResetCodeRequest(
    @SerializedName("number_or_email") val numberOrEmail: String,
)
