package com.netwatcher.polaris.domain.model

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val password: String
)
