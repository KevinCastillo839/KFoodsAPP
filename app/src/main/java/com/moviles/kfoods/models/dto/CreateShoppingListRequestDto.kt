package com.moviles.kfoods.models.dto

data class CreateShoppingListRequestDto(
    val user_id: Int,
    val menu_id: Int,
    val created_at: String,
    val updated_at: String?
)