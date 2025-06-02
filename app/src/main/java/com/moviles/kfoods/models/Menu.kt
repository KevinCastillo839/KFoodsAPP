package com.moviles.kfoods.models

data class Menu(
    val id: Int,
    val name: String,
    val description: String,
    val day_of_week: String,
    val recipes: List<Recipe>

)
