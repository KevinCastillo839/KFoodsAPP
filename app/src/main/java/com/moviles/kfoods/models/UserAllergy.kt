package com.moviles.kfoods.models

data class UserAllergy(
    val id: Int,
    val user_id: Int,
    val allergy_ids: List<Int>,
    val allergie_id: Int = 0,
    val created_at: String?,
    val updated_at: String?,
    val allergy: Allergy? = null,
    val user: User? = null
)
