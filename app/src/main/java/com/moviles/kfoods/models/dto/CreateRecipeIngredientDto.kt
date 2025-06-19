package com.moviles.kfoods.models.dto

data class CreateRecipeIngredientDto(
    val id: Int = 0,             // obligatorio aunque sea 0
    val recipe_id: Int = 0,      // igual 0 al crear
    val ingredient_id: Int,
    val quantity: Double,
    val unit_measurement_id: Int?,
    val created_at: String,
    val updated_at: String
)


