package com.netwatcher.polaris.domain.model

data class User(
    val phone_number : String,
    val email : String,
    val username : String?,
    val image : String?,
    val is_staff : Boolean?,
    val is_banned : Boolean?,
    val date_joined : String?,
    val allow_admin_access : Boolean?
)
