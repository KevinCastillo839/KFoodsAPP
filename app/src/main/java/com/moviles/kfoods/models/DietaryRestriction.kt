package com.moviles.kfoods.models

import com.google.gson.annotations.SerializedName

data class DietaryRestriction(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    val created_at: String?
)
