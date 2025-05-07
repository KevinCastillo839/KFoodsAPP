package com.moviles.kfoods.models
import java.util.*


data class User(
    val id: Int = 0,


    val email: String,


    val password: String, // Contraseña en texto plano


    val full_name: String,

    val created_at: String = Date().toString()
)

