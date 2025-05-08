package com.moviles.kfoods.models.dto

data class ResetPasswordRequest(

    val email: String,
    val token: String,
    val newPassword: String


)