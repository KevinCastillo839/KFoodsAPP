package com.moviles.kfoods.models.dto

data class UpdateIngredientRequestDto(
    val name: String,
    val description: String?,
    val updated_at: String?
)
