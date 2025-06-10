package com.netwatcher.polaris.domain.model

import com.google.gson.annotations.SerializedName
/**
 * Data class representing the request payload for a user sign-up operation.
 * This lives in the domain layer as it's a core business entity.
 *
 * @property numberOrEmail The user's email address or phone number.
 * @property password The user's password.
 * @property code The user's verification code.
 */
data class VerificationRequest(
    @SerializedName("number_or_email") val numberOrEmail: String,
    val password: String,
    val code: String,
)
