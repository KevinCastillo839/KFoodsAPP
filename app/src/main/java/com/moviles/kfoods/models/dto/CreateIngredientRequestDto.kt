package com.moviles.kfoods.models.dto

data class CreateIngredientRequestDto(
    val name: String,
    val description: String?,
    val created_at: String,
    val updated_at: String?
)
