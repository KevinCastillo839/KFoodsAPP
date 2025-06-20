package com.moviles.kfoods.models.dto

data class ShoppingListDto(
    val success: Boolean,
    val data: List<SimpleShoppingListItemDto>,
    val totalItems: Int
)

data class SimpleShoppingListItemDto(
    val ingredient: String,
    val unit: String,
    val totalQuantity: Double
)


