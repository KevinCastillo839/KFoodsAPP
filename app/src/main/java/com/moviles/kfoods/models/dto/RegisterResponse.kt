package com.moviles.kfoods.models.dto

data class RegisterResponse(

    val message: String,
    val userId: Int,
    val token: String
)
