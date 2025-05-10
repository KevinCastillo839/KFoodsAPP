package com.moviles.kfoods.models

data class Preference (
    val id: Int,
    val user_id: Int,
    val is_vegetarian: Boolean,
    val is_gluten_free: Boolean,
    val is_vegan: Boolean,
    val dietary_goals: String,
    val created_at: String?,
    val updated_at: String?
)