package com.moviles.kfoods.models.dto

data class ShoppingListDto(
    val success: Boolean,
    val data: List<SimpleShoppingListItemDto>,
    val totalItems: Int
)

data class SimpleShoppingListItemDto(
    val IngredientName: String,
    val Unit: String,
    val TotalQuantity: Double
)