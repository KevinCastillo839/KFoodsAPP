package com.moviles.kfoods.models

data class Recipe(

    val id: Int,
    val name: String,
    val category: String,
    val preparation_time: Int,
    val image_url: String
)
