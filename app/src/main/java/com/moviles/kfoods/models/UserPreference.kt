package com.moviles.kfoods.models

import com.google.gson.annotations.SerializedName

data class UserPreference(
    @SerializedName("userId") val userId: Int,
    @SerializedName("preferenceId") val preferenceId: Int,
    @SerializedName("restrictions") val restrictions: List<DietaryRestriction>,
    @SerializedName("goal") val goal: DietaryGoal?
)
