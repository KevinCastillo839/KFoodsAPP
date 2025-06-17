package com.moviles.kfoods.network

import com.moviles.kfoods.models.Allergy
import com.moviles.kfoods.models.DietaryGoal
import com.moviles.kfoods.models.DietaryRestriction
import com.moviles.kfoods.models.Preference
import com.moviles.kfoods.models.UserDietaryGoal
import com.moviles.kfoods.models.UserDietaryRestriction
import com.moviles.kfoods.models.UserPreference
import com.moviles.kfoods.models.dto.ApiResponse
import com.moviles.kfoods.models.dto.CreatePreferenceRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PreferenceApiService {

    // Preferences
    @POST("api/preference")
    suspend fun createPreference(@Body request: CreatePreferenceRequestDto): Response<Preference>

    @GET("api/dietary_restriction")
    suspend fun getDietaryRestriction(): Response<List<DietaryRestriction>>

    @GET("api/dietary_goal")
    suspend fun getDietaryGoal(): Response<List<DietaryGoal>>

    @GET("api/dietary_goal/{userId}")
    suspend fun getUserPreferences(@Path("userId") id: Int): Response<UserPreference>

    @POST("api/dietary_restriction")
    suspend fun createDietaryRestriction(@Body request: UserDietaryRestriction): Response<ApiResponse>

    @PUT("api/dietary_restriction")
    suspend fun updateDietaryRestriction(@Body request: UserDietaryRestriction): Response<UserDietaryRestriction>

    @PUT("api/dietary_goal")
    suspend fun updateDietaryGoal(@Body request: UserDietaryGoal): Response<UserDietaryGoal>

    @POST("api/dietary_goal")
    suspend fun createDietaryGoal(@Body request: UserDietaryGoal): Response<ApiResponse>


}