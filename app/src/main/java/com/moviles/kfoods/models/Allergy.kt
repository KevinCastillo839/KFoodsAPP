package com.moviles.kfoods.models

data class Allergy(
    val id: Int,
    val name: String,
    val description: String,
    val created_at: String? = null,
    val updated_at: String? = null
)
