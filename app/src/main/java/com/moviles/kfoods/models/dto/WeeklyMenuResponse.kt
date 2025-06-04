package com.moviles.kfoods.models.dto

import com.moviles.kfoods.models.WeeklyMenu

data class WeeklyMenuResponse(
    val id: Int,
    val created_at: String,
    val weekly_menus: List<WeeklyMenu>
)
