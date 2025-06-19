package com.moviles.kfoods.models.dto

import com.google.gson.annotations.SerializedName

data class ShoppingListDto(
    val success: Boolean,
    val data: List<SimpleShoppingListItemDto>,
    val totalItems: Int
)

data class SimpleShoppingListItemDto(
    @SerializedName("Ingredient")
    val Ingredient: String,
    @SerializedName("Unit")
    val Unit: String,
    @SerializedName("TotalQuantity")
    val TotalQuantity: Double
)
//package com.moviles.kfoods.models.dto

//data class ShoppingListDto(
 //   val success: Boolean,
   // val data: List<SimpleShoppingListItemDto>,
    //val totalItems: Int
//)

//data class SimpleShoppingListItemDto(
 //   val IngredientName: String,
 //   val Unit: String,
   // val TotalQuantity: Double
//)