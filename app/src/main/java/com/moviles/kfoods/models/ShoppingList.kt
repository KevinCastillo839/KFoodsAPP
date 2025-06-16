package com.moviles.kfoods.models

data class ShoppingList(
    val id: Int,
    val recipe_id: Int,
    val user_id: Int,
    val menu_id: Int,
    val created_at: String,
    val updated_at: String?
)