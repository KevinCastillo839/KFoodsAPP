package com.moviles.kfoods.models.dto

data class RecipeIngredientDto(
    val id: Int,
    val recipe_id: Int,
    val ingredient_id: Int,
    val quantity: String,         // Ejemplo: "200 gramos"
    val created_at: String,
    val updated_at: String
)
