package com.moviles.kfoods.models

data class UserDietaryRestriction(
    val id: Int,
    var user_preference_id: Int,
    val restriction_ids: List<Int>
)
