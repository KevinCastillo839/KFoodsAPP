package com.moviles.kfoods.models

data class RecipeIngredient(
    val id: Int,
    val recipe_id: Int,
    val ingredient_id: Int,
    val quantity: String,
    val ingredient: Ingredient,
)
