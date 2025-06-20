package com.moviles.kfoods.models

import com.google.gson.annotations.SerializedName

data class ShoppingList(
    @SerializedName("id")
    val id: Int,
    @SerializedName("recipe_id")
    val recipe_id: Int,
    @SerializedName("user_id")
    val user_id: Int,
    @SerializedName("menu_id")
    val menu_id: Int,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String?
)
