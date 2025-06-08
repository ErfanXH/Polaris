// src/main/java/com/netwatcher/polaris/domain/model/SignUpRequest.kt
package com.netwatcher.polaris.domain.model

/**
 * Data class representing the request payload for a user sign-up operation.
 * This lives in the domain layer as it's a core business entity.
 *
 * @property email The user's email address.
 * @property phoneNumber The user's phone number (nullable, as it's optional).
 * @property password The user's password.
 */
data class SignUpRequest(
    val email: String,
    val phoneNumber: String?, // Nullable as it's optional
    val password: String
)
