package com.moviles.kfoods.models.dto

import com.google.gson.annotations.SerializedName

data class ShoppingListDto(
    val success: Boolean,
    val data: List<SimpleShoppingListItemDto>,
    val totalItems: Int
)

data class SimpleShoppingListItemDto(
    @SerializedName("Ingredient")
    val ingredient: String,
    @SerializedName("Unit")
    val unit: String,
    @SerializedName("TotalQuantity")
    val totalQuantity: Double
)

