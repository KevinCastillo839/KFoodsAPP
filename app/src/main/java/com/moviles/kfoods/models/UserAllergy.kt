package com.moviles.kfoods.models

data class UserAllergy(
    val id: Int,
    val user_id: Int,
    val allergy_ids: List<Int>,
    val created_at: String?,
    val updated_at: String?
)
