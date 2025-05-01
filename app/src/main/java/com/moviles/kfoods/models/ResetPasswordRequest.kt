package com.moviles.kfoods.models

data class ResetPasswordRequest(

    val email: String,
    val token: String,
    val newPassword: String


)
