package com.moviles.kfoods.models

import com.google.gson.annotations.SerializedName

// Nota: Esta clase podría no ser necesaria para la funcionalidad de la lista de compras actual,
// ya que el endpoint by-user/{userId} devuelve ShoppingListDto. Mantener si se usa en otras partes.
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

//package com.moviles.kfoods.models

//data class ShoppingList(
 //   val id: Int,
 //   val recipe_id: Int,
  //  val user_id: Int,
   // val menu_id: Int,
   // val created_at: String,
   // val updated_at: String?
//)