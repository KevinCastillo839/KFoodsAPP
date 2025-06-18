package com.moviles.kfoods.models.dto



data class RecipeDto(
    val id: Int,
    val name: String,
    val instructions: String,
    val image_url: String?,
    val category: String,
    val preparation_time: Int,
    val created_at: String,
    val updated_at: String,
    val user_id: Int,
    val Recipe_Ingredients: List<RecipeIngredientDto>
)






