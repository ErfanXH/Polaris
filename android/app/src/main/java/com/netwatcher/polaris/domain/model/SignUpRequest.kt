package com.netwatcher.polaris.domain.model

import com.google.gson.annotations.SerializedName
/**
 * Data class representing the request payload for a user sign-up operation.
 * This lives in the domain layer as it's a core business entity.
 *
 * @property email The user's email address.
 * @property phoneNumber The user's phone number.
 * @property password The user's password.
 */
data class SignUpRequest(
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val password: String
)
