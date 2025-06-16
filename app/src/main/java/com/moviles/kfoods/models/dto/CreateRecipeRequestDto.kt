package com.moviles.kfoods.models.dto

import okhttp3.MultipartBody

data class CreateRecipeRequestDto(
    val name: String,
    val instructions: String,
    val category: String,
    val preparation_time: Int,
    val image_url: String?,  // si usas imagen
    val created_at: String,
    val updated_at: String?,
    val Recipe_IngredientsJson: String,   // Este es un JSON string
    val user_id: Int?
)


