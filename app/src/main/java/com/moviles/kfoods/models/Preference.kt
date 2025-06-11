package com.moviles.kfoods.models

import com.google.gson.annotations.SerializedName

data class Preference(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("user") val user: User? = null,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String? = null
)
