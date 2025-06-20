package com.moviles.kfoods.models

data class RecipeIngredient(
    val id: Int,
    val recipe_id: Int,
    val ingredient_id: Int,
    val unit_measurement_id: Int?,
    val quantity: Double,
    val ingredient: Ingredient,
)
