package com.moviles.kfoods.models

import com.google.gson.annotations.SerializedName

data class DietaryGoal(
    @SerializedName("id") val id: Int,
    @SerializedName("goal") val goal: String,
    val created_at: String?
)
