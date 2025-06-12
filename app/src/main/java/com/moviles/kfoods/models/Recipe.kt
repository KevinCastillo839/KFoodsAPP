package com.moviles.kfoods.models

import com.google.gson.annotations.SerializedName

data class Recipe(

    val id: Int,
    val name: String,
    val instructions: String,
    val category: String,
    val preparation_time: Int,
    val image_url: String,
    @SerializedName("recipe_Ingredients")
    val recipe_ingredients: List<RecipeIngredient>?
)
