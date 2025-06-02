package com.moviles.kfoods.models

data class WeeklyMenu(
    val id: Int,
    val day_of_week: String,
    val menu: Menu
)
